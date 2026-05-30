import { useState, useEffect } from 'react'
import './RightPanel.css'

export default function RightPanel({ state, onShareSession, linkCopied, onCommand, collapsed, onToggleCollapse }) {
  const [sessionSecs, setSessionSecs] = useState(0)
  const [showWarning, setShowWarning] = useState(false)

  useEffect(() => {
    const timer = setInterval(() => {
      setSessionSecs(prev => prev + 1)
    }, 1000)
    return () => clearInterval(timer)
  }, [])

  const formatSessionTime = () => {
    const hrs = Math.floor(sessionSecs / 3600).toString().padStart(2, '0')
    const mins = Math.floor((sessionSecs % 3600) / 60).toString().padStart(2, '0')
    const secs = (sessionSecs % 60).toString().padStart(2, '0')
    return `${hrs}:${mins}:${secs}`
  }

  const getCognitiveLoad = () => {
    if (!state) return 24
    const thirstLoad = (getThirst() / 100) * 28
    const dnaLoad = (state.dnaTaskActive && state.dnaMovesLeft > 0)
      ? ((10 - Math.min(state.dnaMovesLeft, 10)) / 10) * 35
      : 0
    const cargoLoad = Math.min(items.length * 3, 18)
    const sanityLoad = ((100 - getSanity()) / 100) * 14
    return Math.min(95, Math.max(8, thirstLoad + dnaLoad + cargoLoad + sanityLoad))
  }

  const getThirst = () => {
    if (!state) return 10
    return Math.min(100, (state.hungerMoveCounter || 0) * 10)
  }

  const getSanity = () => {
    if (!state) return 90
    let base = 95
    if (state.dnaTaskActive) base -= 15
    if (state.hungerMoveCounter > 5) base -= 20
    return Math.max(12, base)
  }

  const items = state?.inventory?.items || []

  const hasPhone = items.some(i => i.name.toLowerCase().includes('phone'))
  const hasMic = items.some(i => i.name.toLowerCase().includes('micro') || i.name.toLowerCase().includes('mic'))
  const hasGuitar = items.some(i => i.name.toLowerCase().includes('guitar'))
  const hasSheet = items.some(i => i.name.toLowerCase().includes('sheet') || i.name.toLowerCase().includes('music'))
  const hasDNA = items.some(i => i.name.toLowerCase().includes('batman') || i.name.toLowerCase().includes('dna'))
  const hasSalmon = items.some(i => i.name.toLowerCase().includes('salmon'))
  const hasSnakes = items.some(i => i.name.toLowerCase().includes('treasure'))

  const getItemDetails = (item) => {
    const name = item.name.toUpperCase()
    if (name.includes('PHONE'))
      return { label: 'PHONE', status: 'SYNC', statusType: 'ok', icon: '📱', note: '184 KB' }
    if (name.includes('TREASURE'))
      return { label: 'TREASURE BOX', status: 'SEALED', statusType: 'warn', icon: '📦', note: 'SNAKES' }
    if (name.includes('BATMAN') || name.includes('DNA'))
      return {
        label: 'DNA SAMPLE',
        status: state.dnaMovesLeft > 0 ? 'CRITICAL' : 'SPOILED',
        statusType: 'critical',
        icon: '🧬',
        note: `${state.dnaMovesLeft ?? 0} MOVES LEFT`
      }
    if (name.includes('SALMON'))
      return { label: 'SALMON', status: 'SECURED', statusType: 'ok', icon: '🐟', note: 'ENDANGERED' }
    if (name.includes('MACBOOK'))
      return { label: 'MACBOOK PRO', status: 'FOUND', statusType: 'ok', icon: '💻', note: 'RETURN IT' }
    if (name.includes('GUITAR'))
      return { label: 'GUITAR', status: 'TUNED', statusType: 'ok', icon: '🎸', note: 'GCPA SHOW' }
    if (name.includes('MICRO') || name.includes('MIC'))
      return { label: 'MICROPHONE', status: 'READY', statusType: 'ok', icon: '🎤', note: 'MUSIC SHOW' }
    if (name.includes('SHEET') || name.includes('MUSIC'))
      return { label: 'MUSIC SHEETS', status: 'COMBINED', statusType: 'ok', icon: '🎵', note: 'SHEETS SET' }
    if (name.includes('FOOD') || name.includes('COUPON'))
      return { label: 'FOOD COUPON', status: 'VALID', statusType: 'ok', icon: '🍽️', note: 'DINING HALL' }
    if (name.includes('HELP'))
      return { label: 'HELP GUIDE', status: 'AVAIL', statusType: 'ok', icon: '📋', note: 'READ ME' }
    return { label: name.slice(0, 14), status: 'SYNCED', statusType: 'ok', icon: '💎', note: 'CARGO' }
  }

  const statusClass = (type) => {
    if (type === 'critical') return 'inv-status inv-status-critical'
    if (type === 'warn') return 'inv-status inv-status-warn'
    return 'inv-status inv-status-ok'
  }

  return (
    <div className={`right-deck${collapsed ? ' right-deck-collapsed' : ''}`}>

      {!collapsed && (
        <>
          {/* ── 1. Session Dashboard ── */}
          <div className="vitals-panel heavy-panel">
            <div className="rivet rivet-tl"></div><div className="rivet rivet-tr"></div>
            <div className="rivet rivet-bl"></div><div className="rivet rivet-br"></div>

            <div className="panel-header">
              <span className="panel-title">SESSION_DASHBOARD</span>
              <span className="panel-header-icon">HUD_VT</span>
            </div>

            <div className="vitals-compact-body">
              <div className="vc-clock-row">
                <div className="vc-clock-block">
                  <span className="vc-lbl">SESSION_CLOCK</span>
                  <span className="vc-clock">{formatSessionTime()}</span>
                </div>
                <span className="vc-clock-icon">⏱️</span>
              </div>

              <div className="vc-bar-group">
                <div className="vc-bar-labels">
                  <span className="vc-lbl">COGNITIVE_LOAD</span>
                  <span className="vc-bar-val">{Math.round(getCognitiveLoad())}%</span>
                </div>
                <div className="vc-bar-track">
                  <div className="vc-bar-fill" style={{ width: `${getCognitiveLoad()}%` }}></div>
                </div>
              </div>

              <div className="vc-vitals-row">
                <div className="vc-vital">
                  <span className="vc-lbl">{getThirst() > 70 ? '⚠️ THIRST [WARN]' : 'THIRST'}</span>
                  <span className={`vc-vital-val ${getThirst() > 70 ? 'val-danger' : ''}`}>{getThirst()}%</span>
                  <div className={`vc-mini-track ${getThirst() > 70 ? 'track-warn-pulse' : ''}`}>
                    <div className={`vc-mini-fill ${getThirst() > 70 ? 'fill-danger' : ''}`} style={{ width: `${getThirst()}%` }}></div>
                  </div>
                </div>
                <div className="vc-vital-divider"></div>
                <div className="vc-vital">
                  <span className="vc-lbl">{getSanity() < 30 ? '⚠️ SANITY [CRITICAL]' : 'SANITY'}</span>
                  <span className={`vc-vital-val ${getSanity() < 30 ? 'val-danger' : ''}`}>{getSanity()}%</span>
                  <div className={`vc-mini-track ${getSanity() < 30 ? 'track-warn-pulse' : ''}`}>
                    <div className={`vc-mini-fill ${getSanity() < 30 ? 'fill-danger' : ''}`} style={{ width: `${getSanity()}%` }}></div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* ── 2. Inventory / Bio Signatures ── */}
          <div className="biosig-panel heavy-panel">
            <div className="rivet rivet-tl"></div><div className="rivet rivet-tr"></div>
            <div className="rivet rivet-bl"></div><div className="rivet rivet-br"></div>

            <div className="panel-header">
              <span className="panel-title">CARGO_MANIFEST</span>
              <span className="inv-count-badge">{items.length}/6</span>
            </div>

            <div className="inv-body">
              {items.length > 0 ? (
                (() => {
                  const groupedItems = [];
                  const itemsMap = new Map();
                  items.forEach(item => {
                    const key = item.name;
                    if (itemsMap.has(key)) {
                      itemsMap.get(key).count += 1;
                    } else {
                      itemsMap.set(key, { ...item, count: 1 });
                    }
                  });
                  itemsMap.forEach((val) => {
                    groupedItems.push(val);
                  });

                  return (
                    <table className="inv-table">
                      <thead>
                        <tr className="inv-thead-row">
                          <th className="inv-th">ITEM</th>
                          <th className="inv-th inv-th-right">STATUS</th>
                        </tr>
                      </thead>
                      <tbody>
                        {groupedItems.map((item, idx) => {
                          const d = getItemDetails(item)
                          return (
                            <tr key={idx} className="inv-row">
                              <td className="inv-td-item">
                                <span className="inv-icon">{d.icon}</span>
                                <div className="inv-name-group">
                                  <span className="inv-name">
                                    {d.label}
                                    {item.count > 1 && <span className="inv-item-count">x{item.count}</span>}
                                  </span>
                                  <span className="inv-note">{d.note}</span>
                                </div>
                              </td>
                              <td className="inv-td-status">
                                <span className={statusClass(d.statusType)}>{d.status}</span>
                              </td>
                            </tr>
                          )
                        })}
                      </tbody>
                    </table>
                  );
                })()
              ) : (
                <div className="inv-empty">
                  <svg viewBox="0 0 80 50" className="inv-empty-svg">
                    <rect x="20" y="10" width="40" height="30" rx="3" fill="none"
                      stroke="rgba(242,202,80,0.2)" strokeWidth="1.5" strokeDasharray="3 3"/>
                    <line x1="28" y1="22" x2="52" y2="22" stroke="rgba(242,202,80,0.15)" strokeWidth="1"/>
                    <line x1="28" y1="28" x2="44" y2="28" stroke="rgba(242,202,80,0.15)" strokeWidth="1"/>
                  </svg>
                  <span className="inv-empty-txt">CARGO BAY EMPTY</span>
                  <span className="inv-empty-sub">COLLECT ITEMS TO POPULATE</span>
                </div>
              )}
            </div>
          </div>

          {/* ── 3. Active Priorities ── */}
          <div className="priority-panel heavy-panel">
            <div className="rivet rivet-tl"></div><div className="rivet rivet-tr"></div>
            <div className="rivet rivet-bl"></div><div className="rivet rivet-br"></div>

            <div className="panel-header priority-header">
              <span className="panel-title">ACTIVE_PRIORITIES</span>
              <span className="hud-tag hud-tag-error">⚠ HIGH_STAKES</span>
            </div>

            <div className="panel-body priority-body">
              {/* Quest 1: The Music Show */}
              <div className="quest-row">
                <div className={`quest-dot ${state?.musicTaskComplete ? 'dot-done' : 'dot-active'}`}></div>
                <div className="quest-details">
                  <span className="quest-name">The Music Show</span>
                  <span className="quest-location">
                    {state?.musicTaskComplete ? '✓ COMPLETE' : state?.sashaCalled ? 'LILLY_BUILDING' : 'START: CALL SASHA'}
                  </span>
                  {!state?.musicTaskComplete && (
                    <div className="quest-items-telemetry">
                      <span className={`quest-item-badge ${hasPhone ? 'acquired' : 'missing'}`} title="Phone">
                        📱 {hasPhone ? '[SYNCED]' : '[NO_SIGNAL]'}
                      </span>
                      <span className={`quest-item-badge ${hasGuitar ? 'acquired' : 'missing'}`} title="Guitar">
                        🎸 {hasGuitar ? '[SYNCED]' : '[NO_SIGNAL]'}
                      </span>
                      <span className={`quest-item-badge ${hasMic ? 'acquired' : 'missing'}`} title="Microphone">
                        🎤 {hasMic ? '[SYNCED]' : '[NO_SIGNAL]'}
                      </span>
                      <span className={`quest-item-badge ${hasSheet ? 'acquired' : 'missing'}`} title="Music Sheets">
                        🎵 {hasSheet ? '[SYNCED]' : '[NO_SIGNAL]'}
                      </span>
                    </div>
                  )}
                </div>
                {state?.musicTaskComplete && <span className="quest-done-badge">✓</span>}
              </div>

              {/* Quest 2: DNA Delivery */}
              <div className="quest-row">
                <div className={`quest-dot ${state?.dnaTaskComplete ? 'dot-done' : state?.dnaTaskActive ? 'dot-critical' : 'dot-idle'}`}></div>
                <div className="quest-details">
                  <span className="quest-name">DNA Delivery</span>
                  <span className={`quest-location ${state?.dnaTaskActive ? 'loc-alert' : ''}`}>
                    {state?.dnaTaskComplete ? '✓ COMPLETE' : state?.dnaTaskActive ? `⚠ ${state.dnaMovesLeft} MOVES LEFT` : 'NOT STARTED'}
                  </span>
                  {!state?.dnaTaskComplete && (
                    <div className="quest-items-telemetry">
                      <span className={`quest-item-badge ${hasDNA ? 'acquired' : 'missing'}`} title="DNA Sample">
                        🧬 {hasDNA ? '[SYNCED]' : '[NO_SIGNAL]'}
                      </span>
                    </div>
                  )}
                </div>
                {state?.dnaTaskComplete && <span className="quest-done-badge">✓</span>}
              </div>

              {/* Quest 4: MacBook Return */}
              <div className="quest-row">
                <div className={`quest-dot ${state?.macbookTaskComplete ? 'dot-done' : 'dot-idle'}`}></div>
                <div className="quest-details">
                  <span className="quest-name">MacBook Return</span>
                  <span className="quest-location">
                    {state?.macbookTaskComplete ? '✓ COMPLETE' : 'JULIAN → ADMIN BUILDING'}
                  </span>
                  {!state?.macbookTaskComplete && (
                    <div className="quest-items-telemetry">
                      <span className={`quest-item-badge ${items.some(i => i.name.toLowerCase().includes('macbook')) ? 'acquired' : 'missing'}`} title="MacBook">
                        💻 {items.some(i => i.name.toLowerCase().includes('macbook')) ? '[SYNCED]' : '[NO_SIGNAL]'}
                      </span>
                    </div>
                  )}
                </div>
                {state?.macbookTaskComplete && <span className="quest-done-badge">✓</span>}
              </div>

              {/* Quest 5: Treadmill Challenge */}
              <div className="quest-row">
                <div className={`quest-dot ${state?.treadmillUsed ? 'dot-done' : 'dot-idle'}`}></div>
                <div className="quest-details">
                  <span className="quest-name">Treadmill Challenge</span>
                  <span className="quest-location">
                    {state?.treadmillUsed ? '✓ COMPLETE' : 'LILLY BUILDING'}
                  </span>
                </div>
                {state?.treadmillUsed && <span className="quest-done-badge">✓</span>}
              </div>

              {/* Quest 3: Wildlife Ops */}
              <div className="quest-row">
                <div className={`quest-dot ${(state?.salmonTaskComplete && state?.snakeTaskComplete) ? 'dot-done' : (state?.salmonTaskComplete || state?.snakeTaskComplete) ? 'dot-active' : 'dot-idle'}`}></div>
                <div className="quest-details">
                  <span className="quest-name">Wildlife Ops</span>
                  <span className="quest-location">
                    {(state?.salmonTaskComplete && state?.snakeTaskComplete)
                      ? '✓ COMPLETE'
                      : (state?.salmonTaskComplete || state?.snakeTaskComplete)
                        ? `🐟 ${state?.salmonTaskComplete ? 'DONE' : 'PENDING'} · 🐍 ${state?.snakeTaskComplete ? 'DONE' : 'PENDING'}`
                        : 'HOOVER + THE DUCK'
                    }
                  </span>
                  {(!state?.salmonTaskComplete || !state?.snakeTaskComplete) && (
                    <div className="quest-items-telemetry">
                      {!state?.salmonTaskComplete && (
                        <span className={`quest-item-badge ${hasSalmon ? 'acquired' : 'missing'}`} title="Endangered Salmon">
                          🐟 {hasSalmon ? '[SYNCED]' : '[NO_SIGNAL]'}
                        </span>
                      )}
                      {!state?.snakeTaskComplete && (
                        <span className={`quest-item-badge ${hasSnakes ? 'acquired' : 'missing'}`} title="Treasure Box (Snakes)">
                          📦 {hasSnakes ? '[SYNCED]' : '[NO_SIGNAL]'}
                        </span>
                      )}
                    </div>
                  )}
                </div>
                {(state?.salmonTaskComplete && state?.snakeTaskComplete) && <span className="quest-done-badge">✓</span>}
              </div>
            </div>
          </div>
        </>
      )}

      {showWarning && (
        <div className="session-warning-card">
          <div className="sw-title">⚠ SESSION PERSISTENCE</div>
          <div className="sw-body">
            Your session lives on the server while it's online. Use the SHARE_LINK button to copy a link — paste it in any browser on any device to resume exactly where you left off. Full database persistence (survives server restarts) is coming soon.
          </div>
          <button className="sw-dismiss" onClick={() => setShowWarning(false)}>DISMISS</button>
        </div>
      )}

      <div className="right-panel-footer">
        <button className="footer-icon-btn" onClick={() => onCommand?.('status')} title="OPERATOR STATS">
          <svg viewBox="0 0 20 16" fill="none">
            <path d="M 2 13 L 2 8 M 7 13 L 7 5 M 12 13 L 12 2" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round"/>
          </svg>
        </button>
        <button className="footer-icon-btn" onClick={onToggleCollapse} title={collapsed ? 'EXPAND PANEL' : 'MINIMIZE PANEL'}>
          <svg viewBox="0 0 20 16" fill="none">
            <rect x="2" y="2" width="16" height="12" rx="1" fill="none" stroke="currentColor" strokeWidth="1.5"/>
            {collapsed
              ? <><line x1="10" y1="5" x2="10" y2="11" stroke="currentColor" strokeWidth="1.5"/><line x1="7" y1="8" x2="13" y2="8" stroke="currentColor" strokeWidth="1.5"/></>
              : <line x1="5" y1="8" x2="15" y2="8" stroke="currentColor" strokeWidth="1.5"/>
            }
          </svg>
        </button>
        <button className="footer-icon-btn footer-share-btn" onClick={onShareSession} title="SHARE SESSION LINK">
          <svg viewBox="0 0 20 16" fill="none">
            <circle cx="4" cy="8" r="2" fill="currentColor"/>
            <circle cx="16" cy="3" r="2" fill="currentColor"/>
            <circle cx="16" cy="13" r="2" fill="currentColor"/>
            <line x1="5.8" y1="7.1" x2="14.2" y2="3.9" stroke="currentColor" strokeWidth="1.2"/>
            <line x1="5.8" y1="8.9" x2="14.2" y2="12.1" stroke="currentColor" strokeWidth="1.2"/>
          </svg>
          {linkCopied && <span className="footer-copied-flash">COPIED</span>}
        </button>
        <button className="footer-icon-btn footer-warn-btn" onClick={() => setShowWarning(w => !w)} title="SESSION INFO">
          <svg viewBox="0 0 20 16" fill="none">
            <polygon points="10,1 19,15 1,15" fill="none" stroke="var(--color-error)" strokeWidth="1.5"/>
            <line x1="10" y1="6" x2="10" y2="10" stroke="var(--color-error)" strokeWidth="1.5"/>
            <circle cx="10" cy="12.5" r="0.75" fill="var(--color-error)"/>
          </svg>
        </button>
      </div>
    </div>
  )
}
