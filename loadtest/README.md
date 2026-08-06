# Zork v2 — Load Test

A zero-dependency load test for the game server. It drives the same three HTTP
endpoints the React frontend uses (`/game/start`, `/game/command`, `/game/state`)
to verify the concurrent-session cap, measure per-session memory, and record
latency under sustained load.

No install step — it uses Node's built-in global `fetch` (Node 18+).

## What it does

- **Stage A — ramp to capacity.** Fires `TARGET` `POST /game/start` requests at
  concurrency `CONC`. The server caps live sessions at `MAX_SESSIONS = 400`
  (see [`SessionManager`](../myproject/src/main/java/com/mygroup/SessionManager.java)),
  so it expects ~400 `200`s and the rest `503` ("at capacity"). This verifies
  admission control and leaves the server full for the memory measurement.
- **Stage B — steady load.** With the server full, it sends a command + a HUD
  poll for every live session, `CONC2` at a time, `ROUNDS` times, then reports
  latency percentiles (p50/p95/p99), throughput, error counts, and average
  `/game/state` payload size.

## Running it

Start the server locally (in-memory H2, never touches production):

```bash
cd myproject && mvn -q package -DskipTests
PORT=8123 java -jar target/myproject-1.0-SNAPSHOT.jar
```

Then, from the repo root:

```bash
node loadtest/loadtest.mjs
# or tune it:
TARGET=500 CONC=100 CONC2=300 ROUNDS=4 node loadtest/loadtest.mjs
```

| Env var  | Default | Meaning                                   |
|----------|---------|-------------------------------------------|
| `BASE`   | `http://localhost:8123` | Server base URL             |
| `TARGET` | `500`   | `/game/start` attempts (exceeds the cap)  |
| `CONC`   | `60`    | Concurrency during the ramp               |
| `CONC2`  | `100`   | Concurrency during steady load            |
| `ROUNDS` | `3`     | Command+poll rounds per live session      |

To measure per-session memory, sample the server's resident set while it's full:

```bash
ps -o rss= -p "$(lsof -ti tcp:8123)" | awk '{printf "%.1f MB\n", $1/1024}'
```

> ⚠️ **Point this at a local server.** Running Stage A against production fills
> the real 400-session cap and blocks actual players until those sessions are
> removed.

## Results (local: Apple Silicon, in-memory H2)

Representative run, `TARGET=500 CONC=100 CONC2=300 ROUNDS=4`:

**Admission control**

| Metric                 | Result |
|------------------------|--------|
| Live worlds created    | 400 (exactly — cap holds) |
| Overflow starts        | 100 → `503` |
| Errors                 | 0 |

**Memory**

| State                  | Server RSS |
|------------------------|-----------|
| 0 sessions (baseline)  | ~260–300 MB (JVM) |
| 400 sessions (full)    | +~37–43 MB → **~95–110 KB per session** |

400 game worlds cost only ~40 MB — they fit comfortably inside a 512 MB
instance; the JVM baseline dominates, not the sessions.

**Latency under load** (400 live sessions, concurrency 300)

| Endpoint | p50 | p95 | p99 | max | errors |
|----------|-----|-----|-----|-----|--------|
| `/game/command` | 51 ms | 126 ms | 155 ms | 158 ms | 0 |
| `/game/state`   | 39 ms | 118 ms | 136 ms | 150 ms | 0 |

~5,400 req/s sustained, zero dropped requests, `/game/state` steady at ~3 KB.
Pushing concurrency past Tomcat's default 200-thread pool raises latency but
requests queue rather than fail.

### Caveats

These numbers are a **best case**: localhost (no network latency), in-memory
H2, and far more CPU than the production Render instance. The **memory** result
transfers directly (RAM is RAM); the **latency** result does not — expect higher
latency in production, which is CPU-bound. Gameplay endpoints don't touch the
database, so this does not exercise the Supabase leaderboard path.

### Note on the cap

An earlier version enforced the cap with a non-atomic `size()` check-then-`put`,
which let concurrent bursts overshoot to 403. The cap is now claimed atomically
via an `AtomicInteger`, and this test confirms it holds at exactly 400 even at
concurrency 300.

### Session lifecycle

Sessions are also reclaimed when idle: a scheduled sweep evicts any session
untouched for longer than a 60-minute TTL, so the cap reflects *live* players
rather than every game ever started. An open tab keeps itself alive through the
HUD's `/game/state` polling, so only genuinely-abandoned games are swept.
