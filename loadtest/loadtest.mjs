// Zork v2 load test — zero-dependency, uses Node's built-in global fetch (Node 18+).
// Drives the exact HTTP surface the React frontend uses:
//   POST /game/start   -> create a session (one full game world)
//   POST /game/command -> send a typed command
//   GET  /game/state   -> poll the HUD JSON
//
// Stage A (ramp):   attempt TARGET starts at concurrency CONC. Because the backend
//                   caps live sessions at MAX_SESSIONS=400, we expect ~400 HTTP 200s
//                   and the remainder HTTP 503 ("at capacity"). This verifies
//                   admission control and leaves the server full so per-session
//                   memory can be measured externally (see README).
// Stage B (steady): with the server full, fire a command + state poll for every live
//                   session, CONC2 at a time, ROUNDS times, and report latency
//                   percentiles + throughput under sustained load.
//
// Usage:
//   node loadtest.mjs
//   TARGET=500 CONC=100 CONC2=300 ROUNDS=4 node loadtest.mjs
//   BASE=http://localhost:8123 node loadtest.mjs
//
// NOTE: point this at a LOCAL server. Running Stage A against production fills the
// real session cap and blocks actual players until those sessions are removed.

const BASE   = process.env.BASE   || 'http://localhost:8123';
const TARGET = +(process.env.TARGET || 500);   // start attempts (exceeds the 400 cap on purpose)
const CONC   = +(process.env.CONC   || 60);    // concurrency during ramp
const CONC2  = +(process.env.CONC2  || 100);   // concurrency during steady load
const ROUNDS = +(process.env.ROUNDS || 3);     // steady-load command rounds per session

// ---- tiny stats helpers ----
const pct = (arr, p) => {
  if (!arr.length) return 0;
  const s = [...arr].sort((a, b) => a - b);
  return s[Math.min(s.length - 1, Math.floor((p / 100) * s.length))];
};
const summarize = (name, arr) => {
  if (!arr.length) { console.log(`  ${name.padEnd(16)} (no samples)`); return; }
  const avg = arr.reduce((a, b) => a + b, 0) / arr.length;
  console.log(
    `  ${name.padEnd(16)} n=${String(arr.length).padStart(5)}  ` +
    `avg=${avg.toFixed(1).padStart(6)}ms  p50=${pct(arr,50).toString().padStart(5)}  ` +
    `p95=${String(pct(arr,95)).padStart(5)}  p99=${String(pct(arr,99)).padStart(5)}  ` +
    `max=${String(Math.max(...arr)).padStart(5)}ms`
  );
};

// Run async task fn over items[] with at most `limit` in flight.
async function pool(items, limit, fn) {
  let i = 0;
  const workers = Array.from({ length: Math.min(limit, items.length) }, async () => {
    while (i < items.length) {
      const idx = i++;
      await fn(items[idx], idx);
    }
  });
  await Promise.all(workers);
}

const timed = async (fn) => {
  const t0 = performance.now();
  const r = await fn();
  return { ms: Math.round(performance.now() - t0), r };
};

// ---------- Stage A: ramp to capacity ----------
async function ramp() {
  const startLat = [];
  const live = [];                                  // sessionIds that came back 200
  const counts = { ok: 0, capacity503: 0, error: 0 };
  const errors = new Map();

  const t0 = performance.now();
  await pool([...Array(TARGET).keys()], CONC, async (n) => {
    try {
      const { ms, r } = await timed(() => fetch(`${BASE}/game/start`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ callsign: `LOADTEST_${n}` }),
      }));
      startLat.push(ms);
      if (r.status === 200) {
        const j = await r.json();
        if (j.sessionId) { live.push(j.sessionId); counts.ok++; }
        else { counts.error++; errors.set('200-no-sessionId', (errors.get('200-no-sessionId') || 0) + 1); }
      } else if (r.status === 503) {
        counts.capacity503++;
      } else {
        counts.error++;
        errors.set(`http-${r.status}`, (errors.get(`http-${r.status}`) || 0) + 1);
      }
    } catch (e) {
      counts.error++;
      errors.set(e.code || e.name || 'exception', (errors.get(e.code || e.name || 'exception') || 0) + 1);
    }
  });
  const wall = (performance.now() - t0) / 1000;

  console.log(`\n=== STAGE A — ramp: ${TARGET} start attempts @ concurrency ${CONC} ===`);
  console.log(`  wall=${wall.toFixed(2)}s   throughput=${(TARGET / wall).toFixed(1)} starts/s`);
  console.log(`  200 OK (live world)   : ${counts.ok}`);
  console.log(`  503 at-capacity       : ${counts.capacity503}`);
  console.log(`  other errors          : ${counts.error}` + (errors.size ? `  ${JSON.stringify(Object.fromEntries(errors))}` : ''));
  summarize('start latency', startLat);
  return live;
}

// ---------- Stage B: steady-state load on a full server ----------
async function steady(live) {
  console.log(`\n=== STAGE B — steady load: ${live.length} live sessions x ${ROUNDS} rounds @ concurrency ${CONC2} ===`);
  const cmds = ['look', 'inventory', 'score'];      // universal, location-independent, never error
  const cmdLat = [], stateLat = [];
  let cmdErr = 0, stateErr = 0, stateBytes = 0, stateSamples = 0;

  const t0 = performance.now();
  for (let round = 0; round < ROUNDS; round++) {
    await pool(live, CONC2, async (sid, idx) => {
      // one command
      try {
        const { ms, r } = await timed(() => fetch(`${BASE}/game/command`, {
          method: 'POST',
          headers: { 'X-Session-ID': sid, 'Content-Type': 'text/plain' },
          body: cmds[(idx + round) % cmds.length],
        }));
        if (r.ok) { cmdLat.push(ms); await r.text(); } else { cmdErr++; await r.text(); }
      } catch { cmdErr++; }
      // one HUD poll
      try {
        const { ms, r } = await timed(() => fetch(`${BASE}/game/state`, {
          headers: { 'X-Session-ID': sid },
        }));
        if (r.ok) { stateLat.push(ms); const body = await r.text(); stateBytes += body.length; stateSamples++; }
        else { stateErr++; await r.text(); }
      } catch { stateErr++; }
    });
  }
  const wall = (performance.now() - t0) / 1000;
  const total = cmdLat.length + stateLat.length;

  console.log(`  wall=${wall.toFixed(2)}s   throughput=${(total / wall).toFixed(0)} req/s   (command+state requests: ${total})`);
  summarize('command', cmdLat);
  summarize('state', stateLat);
  console.log(`  command errors=${cmdErr}  state errors=${stateErr}  avg /state size=${stateSamples ? (stateBytes / stateSamples / 1024).toFixed(2) : '?'} KB`);
}

(async () => {
  console.log(`Target ${BASE} | TARGET=${TARGET} CONC=${CONC} CONC2=${CONC2} ROUNDS=${ROUNDS} | node ${process.version}`);
  const live = await ramp();
  if (live.length) await steady(live);
  console.log('\nDone.');
})().catch(e => { console.error(e); process.exit(1); });
