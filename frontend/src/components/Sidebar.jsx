import { useState, useEffect } from 'react'
import { playTick, playScan } from '../utils/audio'
import './Sidebar.css'

export default function Sidebar({ state, onCommand, lastCommand, callsign }) {
  const [lastLoc, setLastLoc] = useState(state?.currLocation?.name || '')
  const [isScanning, setIsScanning] = useState(false)
  const [hoverDir, setHoverDir] = useState(null)

  useEffect(() => {
    if (state?.currLocation?.name && state.currLocation.name !== lastLoc) {
      setLastLoc(state.currLocation.name)
      setIsScanning(true)
      playScan()
      const timer = setTimeout(() => setIsScanning(false), 1200)
      return () => clearTimeout(timer)
    }
  }, [state?.currLocation?.name, lastLoc])

  // snap needle to direction of movement before snapping back smoothly
  useEffect(() => {
    if (lastCommand) {
      const cmd = lastCommand.toLowerCase().trim();
      let dir = null;
      if (cmd.includes('north') || cmd === 'go n') dir = 'north';
      else if (cmd.includes('east') || cmd === 'go e') dir = 'east';
      else if (cmd.includes('south') || cmd === 'go s') dir = 'south';
      else if (cmd.includes('west') || cmd === 'go w') dir = 'west';

      if (dir) {
        setHoverDir(dir);
        const snapTimer = setTimeout(() => {
          setHoverDir(null);
        }, 850); // snap back to 0 or first available exit after 850ms
        return () => clearTimeout(snapTimer);
      }
    }
  }, [lastCommand]);

  const getLocationIntel = (name) => {
    if (!name) return 'Awaiting field signal...'
    const n = name.toLowerCase()
    if (n.includes('east college'))  return 'The historic heart of DePauw since 1877. Something ancient feels like it belongs here — check the HistoryWall.'
    if (n.includes('stadium'))       return 'Byron P. Hollett Little Giant Stadium. The roar of the crowd echoes. Legends were made here.'
    if (n.includes('julian'))        return 'Percy Lavon Julian\'s Science Building — named after DePauw\'s most celebrated alumnus. A mysterious DisplayCase sits in the corner.'
    if (n.includes('hoover'))        return 'Kitchen staff on edge today. A microphone went missing at lunch — no one\'s saying who took it.'
    if (n.includes('gcpa'))          return 'The stage is set and the crowd is gathering. The legend\'s performance depends on what you find.'
    if (n.includes('olin'))          return 'PCR machines humming quietly. Something cold is sealed inside. Biology never sleeps.'
    if (n.includes('cdi'))           return 'Open doors, quiet voices. A phone buzzes somewhere near the back — pick it up.'
    if (n.includes('lilly'))         return 'Athletes pushing limits. The treadmill glows green — no one has stepped on it yet today.'
    if (n.includes('duck'))          return 'Jazz warming up for Thursday. A sealed box sits unnoticed among the performers\' equipment.'
    if (n.includes('roy') || n.includes('library')) return 'Pages turning, whispers only. Something is east of here — if you know where to look.'
    if (n.includes('union'))         return 'Cross-campus hub. Students scatter every direction. A crossing point — use it wisely.'
    if (n.includes('admin'))         return 'President\'s office upstairs. The MacBook case sits open near the front desk. Waiting.'
    if (n.includes('mason'))         return 'Dorm energy — loud music, open doors. A shortcut to Admin exists if you know the campus.'
    if (n.includes('reese'))         return 'RES staff patrol the halls. Half a music score is abandoned on a table near the window.'
    if (n.includes('humbert'))       return 'First-years everywhere. The other half of the music score sits folded on a windowsill.'
    return 'Scanning sector...'
  }

  // Compute normalized sector name
  const getSectorName = () => {
    if (!state?.currLocation?.name) return 'MAIN_CHASSIS.DMG'
    return state.currLocation.name
      .toUpperCase()
      .replace(/[^A-Z0-9]/g, '_') + '.DMG'
  }

  // Get exiting connections to light up compass directions
  const exits = state?.currLocation?.connections 
    ? Object.keys(state.currLocation.connections).map(e => e.toLowerCase()) 
    : []

  const isExitAvailable = (dir) => exits.includes(dir)

  const handleDirectionClick = (dir) => {
    if (isExitAvailable(dir)) {
      playTick()
      onCommand(`go ${dir}`)
    }
  }

  const getRotationAngle = () => {
    if (isScanning) return null
    if (hoverDir === 'north') return 0
    if (hoverDir === 'east') return 90
    if (hoverDir === 'south') return 180
    if (hoverDir === 'west') return 270
    
    // Default needle direction points to first available exit
    if (exits.length > 0) {
      if (exits.includes('north')) return 0
      if (exits.includes('east')) return 90
      if (exits.includes('south')) return 180
      if (exits.includes('west')) return 270
    }
    return 0
  }

  const getHeadingText = () => {
    if (isScanning) return 'SCAN'
    const angle = getRotationAngle()
    if (angle === 0) return '0.0° N'
    if (angle === 90) return '90.0° E'
    if (angle === 180) return '180.0° S'
    if (angle === 270) return '270.0° W'
    return '360.0° N'
  }

  return (
    <div className="sidebar heavy-panel">
      {/* Structural rivets */}
      <div className="rivet rivet-tl"></div>
      <div className="rivet rivet-tr"></div>
      <div className="rivet rivet-bl"></div>
      <div className="rivet rivet-br"></div>

      <div className="panel-header">
        <span className="panel-title">OPERATOR_PROFILE</span>
        <span className="panel-header-icon">{(callsign || 'OP_01').substring(0, 6).toUpperCase()}</span>
      </div>

      <div className="panel-body">
        {/* Operator Profile Card */}
        <div className="operator-card">
          <div className="avatar-container">
            <svg viewBox="0 0 100 100" className="avatar-svg">
              <defs>
                <pattern id="grid" width="10" height="10" patternUnits="userSpaceOnUse">
                  <path d="M 10 0 L 0 0 0 10" fill="none" stroke="rgba(242, 202, 80, 0.15)" strokeWidth="0.5"/>
                </pattern>
              </defs>
              <rect width="100" height="100" fill="url(#grid)" />
              {/* High-tech tactical avatar shape */}
              <circle cx="50" cy="38" r="18" fill="none" stroke="var(--color-primary)" strokeWidth="2" />
              <path d="M25,80 C25,60 35,55 50,55 C65,55 75,60 75,80" fill="none" stroke="var(--color-primary)" strokeWidth="2" />
              <line x1="50" y1="10" x2="50" y2="90" stroke="rgba(242, 202, 80, 0.2)" strokeWidth="1" strokeDasharray="2 2" />
              <line x1="10" y1="50" x2="90" y2="50" stroke="rgba(242, 202, 80, 0.2)" strokeWidth="1" strokeDasharray="2 2" />
              <circle cx="50" cy="50" r="44" fill="none" stroke="rgba(242, 202, 80, 0.3)" strokeWidth="1" strokeDasharray="4 4" />
            </svg>
            <div className="scanline-overlay"></div>
          </div>

          <div className="operator-meta">
            <div className="operator-title">{(callsign || 'OPERATOR_01').toUpperCase()}</div>
            <div className="operator-status">ONLINE // SYNC_OK</div>
            <div className="operator-stats-grid">
              <div className="op-stat-lbl">CYCLES:</div>
              <div className="op-stat-val">{(state?.moveCount || 0).toString().padStart(5, '0')}</div>
              
              <div className="op-stat-lbl">EXP:</div>
              <div className="op-stat-val">{(state?.points || 0).toLocaleString()} XP</div>
              
              <div className="op-stat-lbl">SECTOR:</div>
              <div className="op-stat-val truncate-sector" title={getSectorName()}>
                {getSectorName()}
              </div>
            </div>
          </div>
        </div>

        {/* Campus Intel Feed */}
        <div className="command-module-group">
          <div className="small-panel-title">CAMPUS_INTEL</div>
          <div className={`intel-card ${isScanning ? 'intel-flash' : ''}`}>
            <div className="intel-location-tag">
              {state?.currLocation?.name?.toUpperCase() || '—'} · FIELD REPORT
            </div>
            <div className="intel-flavor-text">
              {getLocationIntel(state?.currLocation?.name)}
            </div>
            {state?.dnaTaskActive && (state?.dnaMovesLeft ?? 0) > 0 && (
              <div className="intel-alert intel-critical">
                🧬 DNA SPOILING · {state.dnaMovesLeft} MOVE{state.dnaMovesLeft !== 1 ? 'S' : ''} LEFT
              </div>
            )}
            {(state?.hungerMoveCounter ?? 0) > 5 && (
              <div className="intel-alert intel-warn">
                ⚡ FUEL LOW · FIND FOOD NOW
              </div>
            )}
            {state?.typingChallengeActive && (
              <div className="intel-alert intel-critical">
                ⌨️ CHALLENGE ACTIVE — TYPE!
              </div>
            )}
          </div>
        </div>

        {/* Analog Compass Radar */}
        <div className="analog-compass-group">
          <div className="compass-container">
            <svg viewBox="0 0 100 100" className="compass-dial">
              <circle cx="50" cy="50" r="42" fill="none" stroke="rgba(242, 202, 80, 0.2)" strokeWidth="1" />
              <circle cx="50" cy="50" r="36" fill="none" stroke="rgba(242, 202, 80, 0.4)" strokeWidth="2" strokeDasharray="2 4" />
              {/* Compass ticks */}
              <line x1="50" y1="8" x2="50" y2="14" stroke="var(--color-primary)" strokeWidth="2" />
              <line x1="50" y1="86" x2="50" y2="92" stroke="rgba(242, 202, 80, 0.4)" strokeWidth="1" />
              <line x1="8" y1="50" x2="14" y2="50" stroke="rgba(242, 202, 80, 0.4)" strokeWidth="1" />
              <line x1="86" y1="50" x2="92" y2="50" stroke="rgba(242, 202, 80, 0.4)" strokeWidth="1" />
              
              {/* Moving pointer - rotates dynamically */}
              <g 
                className={isScanning ? 'compass-needle-scanning' : 'compass-needle'}
                style={!isScanning ? { transform: `translate(50px, 50px) rotate(${getRotationAngle()}deg)` } : {}}
              >
                <polygon points="0,-32 6,-10 -6,-10" fill="var(--color-primary)" />
                <polygon points="0,32 5,10 -5,10" fill="rgba(242, 202, 80, 0.3)" />
                <circle cx="0" cy="0" r="4" fill="var(--color-primary)" />
              </g>

              {/* Exits indicators glowing on compass */}
              {isExitAvailable('north') && (
                <circle 
                  cx="50" cy="11" r="3.5" 
                  fill="#00ffcc" 
                  className="pulse-glow" 
                  onMouseEnter={() => { setHoverDir('north'); playTick(); }} 
                  onMouseLeave={() => setHoverDir(null)} 
                  onClick={() => handleDirectionClick('north')}
                  style={{ cursor: 'pointer' }}
                />
              )}
              {isExitAvailable('south') && (
                <circle 
                  cx="50" cy="89" r="3.5" 
                  fill="#00ffcc" 
                  className="pulse-glow" 
                  onMouseEnter={() => { setHoverDir('south'); playTick(); }} 
                  onMouseLeave={() => setHoverDir(null)} 
                  onClick={() => handleDirectionClick('south')}
                  style={{ cursor: 'pointer' }}
                />
              )}
              {isExitAvailable('east') && (
                <circle 
                  cx="89" cy="50" r="3.5" 
                  fill="#00ffcc" 
                  className="pulse-glow" 
                  onMouseEnter={() => { setHoverDir('east'); playTick(); }} 
                  onMouseLeave={() => setHoverDir(null)} 
                  onClick={() => handleDirectionClick('east')}
                  style={{ cursor: 'pointer' }}
                />
              )}
              {isExitAvailable('west') && (
                <circle 
                  cx="11" cy="50" r="3.5" 
                  fill="#00ffcc" 
                  className="pulse-glow" 
                  onMouseEnter={() => { setHoverDir('west'); playTick(); }} 
                  onMouseLeave={() => setHoverDir(null)} 
                  onClick={() => handleDirectionClick('west')}
                  style={{ cursor: 'pointer' }}
                />
              )}
            </svg>

            {/* Radar Sweep sweep line */}
            <div className={`radar-sweep ${isScanning ? 'scanning-fast' : ''}`}></div>
            
            <div className="compass-heading">
              <span className="heading-val">{getHeadingText()}</span>
              <span className="heading-lbl">{hoverDir ? hoverDir.charAt(0).toUpperCase() : 'HUD'}</span>
            </div>
          </div>

          <div className="compass-controls">
            <button 
              className={`compass-btn ${isExitAvailable('west') ? 'active' : 'disabled'}`}
              onClick={() => handleDirectionClick('west')}
              onMouseEnter={() => { setHoverDir('west'); playTick(); }}
              onMouseLeave={() => setHoverDir(null)}
              disabled={!isExitAvailable('west')}
            >
              W
            </button>
            <button 
              className={`compass-btn ${isExitAvailable('north') ? 'active' : 'disabled'}`}
              onClick={() => handleDirectionClick('north')}
              onMouseEnter={() => { setHoverDir('north'); playTick(); }}
              onMouseLeave={() => setHoverDir(null)}
              disabled={!isExitAvailable('north')}
            >
              N
            </button>
            <button 
              className={`compass-btn ${isExitAvailable('east') ? 'active' : 'disabled'}`}
              onClick={() => handleDirectionClick('east')}
              onMouseEnter={() => { setHoverDir('east'); playTick(); }}
              onMouseLeave={() => setHoverDir(null)}
              disabled={!isExitAvailable('east')}
            >
              E
            </button>
          </div>
        </div>

        {/* Neural Load meter */}
        <div className="neural-load-section">
          <div className="neural-lbl-row">
            <span>NEURAL_LOAD</span>
            <span className="neural-pulse-dot"></span>
          </div>
          <div className="neural-bars">
            {Array.from({ length: 12 }).map((_, i) => (
              <div 
                key={i} 
                className={`neural-bar ${i < 8 ? 'active' : ''}`}
                style={{ animationDelay: `${i * 0.05}s` }}
              ></div>
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}
