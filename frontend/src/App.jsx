import { useState, useEffect, useRef } from 'react'
import Sidebar from './components/Sidebar'
import StoryPanel from './components/StoryPanel'
import RightPanel from './components/RightPanel'
import CommandInput from './components/CommandInput'
import { toggleSound, isSoundEnabled, playBeep, playAlarm } from './utils/audio'
import { containsProfanity } from './utils/profanity'
import './App.css'

const now = () => {
  const d = new Date()
  return `${d.getHours().toString().padStart(2,'0')}:${d.getMinutes().toString().padStart(2,'0')}:${d.getSeconds().toString().padStart(2,'0')}`
}

export default function App() {
  const [state, setState] = useState(null)
  const [messages, setMessages] = useState([])
  const [commandHistory, setCommandHistory] = useState([])
  const [historyIndex, setHistoryIndex] = useState(-1)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [activeTab, setActiveTab] = useState('MAP_INTEL')
  const [showHelpModal, setShowHelpModal] = useState(false)
  const [theme, setTheme] = useState('amber')
  const [showSettings, setShowSettings] = useState(false)
  const [soundOn, setSoundOn] = useState(isSoundEnabled())
  const [promptError, setPromptError] = useState(false)
  const [commandPending, setCommandPending] = useState(false)
  const [prevDnaMoves, setPrevDnaMoves] = useState(null)
  const [sessionSecs, setSessionSecs] = useState(0)
  const [callsign, setCallsign] = useState('')
  const [callsignInput, setCallsignInput] = useState('')
  const [callsignError, setCallsignError] = useState('')
  const [showCallsignScreen, setShowCallsignScreen] = useState(true)
  const [linkCopied, setLinkCopied] = useState(false)
  const [rightPanelCollapsed, setRightPanelCollapsed] = useState(false)
  const [leaderboard, setLeaderboard] = useState(null)
  const [showLeaderboard, setShowLeaderboard] = useState(false)
  const [dpuLeaderboard, setDpuLeaderboard] = useState(null)
  const [showDpuLeaderboard, setShowDpuLeaderboard] = useState(false)
  const [activeBg, setActiveBg] = useState(null)
  const [fadingBg, setFadingBg] = useState(null)
  const activeBgRef = useRef(null)
  const pollIntervalRef = useRef(null)
  const sessionIdRef = useRef(null)
  const sessionTimerRef = useRef(null)
  const scoreSavedRef = useRef(false)
  const slideshowIntervalRef = useRef(null)
  const slideshowIndexRef = useRef(0)

  // On mount: resume from a shared URL session or a locally saved session.
  // URL params take priority so share links always restore the right game.
  useEffect(() => {
    const params = new URLSearchParams(window.location.search)
    const urlSessionId = params.get('session')
    const urlCallsign = params.get('c') || ''

    if (urlSessionId) {
      window.history.replaceState({}, '', window.location.pathname)
    }

    const savedId = urlSessionId || localStorage.getItem('zork_session_id')
    const savedSecs = urlSessionId ? 0 : parseInt(localStorage.getItem('zork_session_secs') || '0', 10)
    const savedCallsign = urlCallsign || localStorage.getItem('zork_callsign') || ''

    if (savedId) {
      setLoading(true)
      setShowCallsignScreen(false)
      setCallsign(savedCallsign)
      sessionIdRef.current = savedId
      setSessionSecs(savedSecs)
      fetchState(savedId).then(ok => {
        if (ok) {
          localStorage.setItem('zork_session_id', savedId)
          localStorage.setItem('zork_callsign', savedCallsign)
          const savedMessages = JSON.parse(localStorage.getItem('zork_messages') || '[]')
          setMessages(savedMessages.length > 0 ? savedMessages : [
            { type: 'system', text: 'SESSION RESTORED — Welcome back, ' + (savedCallsign || 'OPERATOR') + '.', time: now() },
            { type: 'system', text: 'Type "look" to reorient yourself.', time: now() }
          ])
          pollIntervalRef.current = setInterval(() => fetchState(sessionIdRef.current), 1500)
        } else {
          localStorage.removeItem('zork_session_id')
          localStorage.removeItem('zork_session_secs')
          localStorage.removeItem('zork_callsign')
          localStorage.removeItem('zork_messages')
          sessionIdRef.current = null
          setShowCallsignScreen(true)
        }
        setLoading(false)
      })
    }
  }, [])

  // Persist message log to localStorage (capped at 300 to stay well under storage limits)
  useEffect(() => {
    if (sessionIdRef.current && messages.length > 0) {
      const toSave = messages.length > 300 ? messages.slice(-300) : messages
      localStorage.setItem('zork_messages', JSON.stringify(toSave))
    }
  }, [messages])

  // Persist session timer to localStorage every 5 seconds
  useEffect(() => {
    if (sessionSecs > 0 && sessionIdRef.current) {
      localStorage.setItem('zork_session_secs', String(sessionSecs))
    }
  }, [sessionSecs])

  // Session clock — pauses when tab is hidden, resumes when visible
  useEffect(() => {
    if (showCallsignScreen || !sessionIdRef.current) return

    const startTimer = () => {
      sessionTimerRef.current = setInterval(() => {
        setSessionSecs(prev => prev + 1)
      }, 1000)
    }

    const handleVisibility = () => {
      if (document.hidden) {
        clearInterval(sessionTimerRef.current)
      } else {
        startTimer()
      }
    }

    startTimer()
    document.addEventListener('visibilitychange', handleVisibility)

    return () => {
      clearInterval(sessionTimerRef.current)
      document.removeEventListener('visibilitychange', handleVisibility)
    }
  }, [showCallsignScreen, callsign])

  // Sync with debrief tab if state finale is shown
  useEffect(() => {
    if (state?.finaleShown) {
      setActiveTab('LOGS')
    }
  }, [state?.finaleShown])

  // Auto-save score to leaderboard when game ends
  useEffect(() => {
    if (state?.finaleShown && !scoreSavedRef.current) {
      scoreSavedRef.current = true
      fetch('/leaderboard/save', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          callsign: callsign || 'OPERATOR_01',
          score: state.points || 0,
          moveCount: state.moveCount || 0
        })
      }).catch(() => {})
    }
  }, [state?.finaleShown])

  // Warning chirp when DNA countdown ticks down
  useEffect(() => {
    if (state?.dnaMovesLeft !== undefined) {
      if (prevDnaMoves !== null && state.dnaMovesLeft < prevDnaMoves && state.dnaMovesLeft > 0) {
        playAlarm()
      }
      setPrevDnaMoves(state.dnaMovesLeft)
    }
  }, [state?.dnaMovesLeft, prevDnaMoves])

  const initGame = async (playerCallsign) => {
    try {
      clearInterval(pollIntervalRef.current)
      clearInterval(sessionTimerRef.current)
      setLoading(true)
      setSessionSecs(0)
      scoreSavedRef.current = false
      const name = playerCallsign || callsign || 'OPERATOR_01'
      const response = await fetch('/game/start', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ callsign: name })
      })
      const data = await response.json()
      const newSessionId = data.sessionId
      sessionIdRef.current = newSessionId
      localStorage.setItem('zork_session_id', newSessionId)
      localStorage.setItem('zork_session_secs', '0')
      localStorage.setItem('zork_callsign', name)
      localStorage.removeItem('zork_messages')
      await fetchState(newSessionId)
      setMessages([
        { type: 'system', text: 'TACTICAL TERMINAL CORE INITIALIZED.', time: now() },
        { type: 'system', text: 'You stand in East College — the crown jewel of DePauw University, built in 1877. Its Victorian Gothic tower has watched over this campus for generations. Seven missions await. The Monon Bell belongs to those who push furthest.', time: now() },
        { type: 'system', text: 'Type "examine help" to read your mission briefing, or "look" to survey your surroundings.', time: now() }
      ])
      setLoading(false)
      pollIntervalRef.current = setInterval(() => fetchState(sessionIdRef.current), 1500)
    } catch (e) {
      setError(e.message)
      setLoading(false)
    }
  }

  // Returns true if state was fetched successfully, false if session was not found
  const fetchState = async (sid) => {
    const id = sid || sessionIdRef.current
    if (!id) return false
    try {
      const response = await fetch('/game/state', {
        headers: { 'X-Session-ID': id }
      })
      if (response.status === 404) return false
      if (!response.ok) throw new Error('Failed to fetch tactical state')
      const data = await response.json()
      setState(data)
      return true
    } catch (e) {
      console.error('Tactical telemetry fetch failure:', e)
      return false
    }
  }

  // Sends the command as plain text (not JSON) to match the Spring Boot @RequestBody String endpoint.
  // commandPending blocks additional input while the server is processing so responses don't pile up.
  const handleCommand = async (command) => {
    if (!command.trim() || commandPending) return

    playBeep()

    const sanitizedCommand = command.trim()

    // Seamless UX Intercept: If command matches help, present the Projected Glass HUD Help Modal!
    const cmdLower = sanitizedCommand.toLowerCase().replace(/[^a-z0-9 ]/g, '').trim()
    if (cmdLower === 'help' || cmdLower === 'examine help' || cmdLower === 'examine help guide' || cmdLower === 'status help') {
      setShowHelpModal(true)
      setMessages(prev => [...prev, { type: 'command', text: `> ${sanitizedCommand}`, time: now() }])
      setMessages(prev => [...prev, { type: 'output', text: 'Opening manual...', time: now() }])
      return
    }

    // Seamless UX Intercept: 'clear' wipes the visible terminal log only — progress/session is untouched
    if (cmdLower === 'clear' || cmdLower === 'cls') {
      setMessages([])
      localStorage.removeItem('zork_messages')
      return
    }

    setCommandPending(true)
    try {
      setCommandHistory(prev => [...prev, sanitizedCommand])
      setHistoryIndex(-1)
      setMessages(prev => [...prev, { type: 'command', text: `> ${sanitizedCommand}`, time: now() }])

      // If user typed quit, switch to logs/debrief tab and save score
      if (sanitizedCommand.toLowerCase() === 'quit') {
        setActiveTab('LOGS')
        if (!scoreSavedRef.current && state) {
          scoreSavedRef.current = true
          fetch('/leaderboard/save', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
              callsign: callsign || 'OPERATOR_01',
              score: state.points || 0,
              moveCount: state.moveCount || 0
            })
          }).catch(() => {})
        }
      }

      const response = await fetch('/game/command', {
        method: 'POST',
        headers: { 'Content-Type': 'text/plain', 'X-Session-ID': sessionIdRef.current },
        body: sanitizedCommand
      })

      const output = await response.text()

      // Unlock input as soon as the command response arrives — user can type again
      setCommandPending(false)

      // Fire state fetch in background so overlay updates without blocking input
      fetchState()

      setMessages(prev => [...prev, { type: 'output', text: output, time: now() }])

      // Detect command recognition errors / fumbles to trigger prompt error flicker
      const lowerOut = output.toLowerCase();
      if (
        lowerOut.includes("not sure") ||
        lowerOut.includes("can't go that way") ||
        lowerOut.includes("no exits") ||
        lowerOut.includes("is not here") ||
        lowerOut.includes("cannot find") ||
        lowerOut.includes("danger") ||
        lowerOut.includes("error")
      ) {
        setPromptError(true)
        setTimeout(() => setPromptError(false), 500)
      }
    } catch (e) {
      setMessages(prev => [...prev, { type: 'error', text: `Telemetry error: ${e.message}`, time: now() }])
      setCommandPending(false)
    }
  }

  const handleCallsignSubmit = () => {
    const name = callsignInput.trim() || 'OPERATOR_01'
    if (containsProfanity(name)) {
      setCallsignError('CALLSIGN REJECTED — KEEP IT CLEAN, OPERATOR.')
      return
    }
    setCallsignError('')
    setCallsign(name)
    setCallsignInput('')
    setShowCallsignScreen(false)
    initGame(name)
  }

  const handleShareSession = () => {
    if (!sessionIdRef.current) return
    const base = window.location.origin + window.location.pathname
    const url = `${base}?session=${sessionIdRef.current}&c=${encodeURIComponent(callsign)}`
    navigator.clipboard.writeText(url).then(() => {
      setLinkCopied(true)
      setTimeout(() => setLinkCopied(false), 2000)
    })
  }

  const handleHistoryNav = (direction) => {
    if (commandHistory.length === 0) return

    if (direction === 'up') {
      setHistoryIndex(prev => Math.min(prev + 1, commandHistory.length - 1))
    } else {
      setHistoryIndex(prev => Math.max(prev - 1, -1))
    }
  }

  const getHistoryCommand = () => {
    if (historyIndex >= 0 && historyIndex < commandHistory.length) {
      return commandHistory[commandHistory.length - 1 - historyIndex]
    }
    return ''
  }

  const formatSessionTime = (totalSecs) => {
    const hrs = Math.floor(totalSecs / 3600).toString().padStart(2, '0')
    const mins = Math.floor((totalSecs % 3600) / 60).toString().padStart(2, '0')
    const secs = (totalSecs % 60).toString().padStart(2, '0')
    return `${hrs}:${mins}:${secs}`
  }

  // Maps point totals to letter-tier labels shown on the debrief screen gauge
  const getDebriefRanking = () => {
    if (!state) return 'C-TIER'
    const pts = state.points || 0
    if (pts >= 60) return 'S-TIER'
    if (pts >= 40) return 'A-TIER'
    if (pts >= 20) return 'B-TIER'
    return 'C-TIER'
  }

  const getMoveAccuracy = () => {
    if (!state || state.moveCount === 0) return '100%'
    const acc = Math.max(12, 100 - (state.moveCount * 0.4))
    return `${acc.toFixed(1)}%`
  }

  // Each badge has its own unlock condition checked against live game state
  const isBadgeUnlocked = (badge) => {
    if (!state) return false
    switch (badge) {
      case 'SPEED_RUNNER':
        return sessionSecs < 360 && state.points > 5 // Under 6 mins
      case 'CAMPUS_LEGEND':
        return state.points >= 40
      case 'GHOST_OPERATOR':
        return state.moveCount > 0 && state.moveCount < 30
      case 'UNTOUCHABLE':
        return state.salmonTaskComplete && state.snakeTaskComplete && !state.typingChallengeActive
      default:
        return false
    }
  }

  const EAST_COLLEGE_IMAGES = [
    '/eastcollege.png', '/eastcollege1.png', '/eastcollege2.png',
    '/eastcollege3.png', '/eastcollege4.png', '/eastcollege5.png'
  ]

  const LOCATION_IMAGES = {
    'east college':           '/eastcollege.png',
    'stadium':                '/monon.png',
  'julian':                 '/julian.jpg',
    'hoover':                 '/hoover.jpg',
    'olin':                   '/olin.jpg',
    'gcpa':                   '/gcpa.jpg',
    'roy library':            '/roylibrary.jpg',
    'cdi':                    '/cdi.jpg',
    'lilly building':         '/lilly.jpg',
    'the fluttering duck':    '/duck.jpg',
    'the union building':     '/ub.jpg',
    'administration building':'/admin.jpg',
    'mason hall':             '/mason.jpg',
    'reese hall':             '/reese.jpg',
    'humbert hall':           '/humbert.jpg',
  }

  const locationKey = state?.currLocation?.name?.toLowerCase() || ''
  const newBg = LOCATION_IMAGES[locationKey] || '/school.jpg'

  useEffect(() => {
    if (newBg === activeBgRef.current) return
    setFadingBg(activeBgRef.current)
    activeBgRef.current = newBg
    setActiveBg(newBg)
    const t = setTimeout(() => setFadingBg(null), 900)
    return () => clearTimeout(t)
  }, [newBg])

  useEffect(() => {
    if (slideshowIntervalRef.current) {
      clearInterval(slideshowIntervalRef.current)
      slideshowIntervalRef.current = null
    }
    if (locationKey === 'east college') {
      slideshowIndexRef.current = 0
      slideshowIntervalRef.current = setInterval(() => {
        slideshowIndexRef.current = (slideshowIndexRef.current + 1) % EAST_COLLEGE_IMAGES.length
        const next = EAST_COLLEGE_IMAGES[slideshowIndexRef.current]
        setFadingBg(activeBgRef.current)
        activeBgRef.current = next
        setActiveBg(next)
        setTimeout(() => setFadingBg(null), 900)
      }, 5000)
    }
    return () => {
      if (slideshowIntervalRef.current) clearInterval(slideshowIntervalRef.current)
    }
  }, [locationKey])

  if (showCallsignScreen) {
    return (
      <div className={`chassis-monitor theme-${theme}`}>
        <div className="callsign-screen">
          <div className="callsign-logo">ZORK_V2</div>
          <div className="callsign-subtitle">TACTICAL_OS · DEPAUW CAMPUS OPERATIONS</div>
          <div className="callsign-card">
            <div className="callsign-card-title">PLAYER SETUP</div>
            <div className="callsign-label">ENTER YOUR NAME</div>
            <input
              className="callsign-input"
              type="text"
              maxLength={32}
              placeholder="e.g. Stephen, ALPHA_01, anything"
              value={callsignInput}
              onChange={e => { setCallsignInput(e.target.value); setCallsignError('') }}
              onKeyDown={e => { if (e.key === 'Enter') handleCallsignSubmit() }}
              autoFocus
            />
            {callsignError && (
              <div style={{ color: '#ff6b6b', fontFamily: 'Share Tech Mono, monospace', fontSize: '11px', letterSpacing: '1px', marginTop: '8px', textAlign: 'center' }}>
                {callsignError}
              </div>
            )}
            <button
              className="mechanical-plate active-yellow callsign-deploy-btn"
              onClick={handleCallsignSubmit}
            >
              START GAME
            </button>
            <div style={{ marginTop: '16px', fontFamily: 'Share Tech Mono, monospace', fontSize: '10px', color: 'var(--color-outline)', textAlign: 'center', lineHeight: '1.6', letterSpacing: '0.5px' }}>
              DePauw student? Add <span style={{ color: 'var(--color-primary)' }}>_dpu</span> to your name<br />
              (e.g. <span style={{ color: 'var(--color-primary)' }}>Stephen_dpu</span>) to appear on the DePauw leaderboard
            </div>
          </div>
        </div>
      </div>
    )
  }

  if (loading) {
    return (
      <div className={`chassis-monitor theme-${theme}`}>
        <div className="hud-loading">
          <div className="hud-loading-spinner"></div>
          <div>LOADING...</div>
        </div>
      </div>
    )
  }

  if (error) {
    return (
      <div className={`chassis-monitor theme-${theme}`}>
        <div className="hud-loading" style={{ color: 'var(--color-error)' }}>
          <div>⚠️ CONNECTION ERROR</div>
          <div style={{ fontSize: '12px', marginTop: '10px' }}>{error}</div>
          <button 
            className="mechanical-plate" 
            style={{ marginTop: '20px' }} 
            onClick={() => initGame(callsign)}
          >
            RETRY
          </button>
        </div>
      </div>
    )
  }

  return (
    <div className={`chassis-monitor theme-${theme}`}>

      {/* Archive theme: SVG filter defs for organic worn-edge displacement */}
      {theme === 'archive' && (
        <svg style={{position:'absolute',width:0,height:0,overflow:'hidden',pointerEvents:'none'}} aria-hidden="true">
          <defs>
            <filter id="archive-worn-edges" x="-5%" y="-5%" width="110%" height="110%">
              <feTurbulence type="fractalNoise" baseFrequency="0.04 0.05" numOctaves="4" seed="12" result="noise"/>
              <feDisplacementMap in="SourceGraphic" in2="noise" scale="3" xChannelSelector="R" yChannelSelector="G"/>
            </filter>
          </defs>
        </svg>
      )}

      {/* Archive theme: torn paper / degraded edge overlay */}
      {theme === 'archive' && <div className="archive-torn-overlay" aria-hidden="true" />}

      <div className="small-screen-overlay">
        <div className="ss-icon">[ ! ]</div>
        <div className="ss-divider" />
        <div className="ss-title">DISPLAY_ALERT</div>
        <div className="ss-body">
          TACTICAL_OS is optimized for desktop displays.<br />
          Please maximize your browser window or switch to a larger screen for the full experience.
        </div>
        <div className="ss-divider" />
      </div>
      {/* 1. Header Bar */}
      <header className="chassis-header">
        <div className="logo-section">
          <span className="logo-icon"></span>
          <span className="logo-text">ZORK_V2</span>
          <span className="logo-version">TACTICAL_OS · DEPAUW</span>
        </div>

        <nav className="nav-tabs">
          <div 
            className={`nav-tab ${activeTab === 'MAP_INTEL' ? 'active' : ''}`}
            onClick={() => setActiveTab('MAP_INTEL')}
          >
            MAP
          </div>
          <div 
            className={`nav-tab ${activeTab === 'LOGS' ? 'active' : ''}`}
            onClick={() => setActiveTab('LOGS')}
          >
            DEBRIEF
          </div>
          <div 
            className={`nav-tab ${showHelpModal ? 'active' : ''}`}
            onClick={() => setShowHelpModal(true)}
          >
            MANUAL
          </div>
        </nav>

        <div className="header-utils">
          <div className="link-strength">
            <span>LINK_STRENGTH</span>
            <div className="bars">
              <div className="bar-indicator active"></div>
              <div className="bar-indicator active"></div>
              <div className="bar-indicator active"></div>
            </div>
          </div>
          <button 
            className={`icon-button sound-toggle-btn ${soundOn ? 'sound-active' : ''}`} 
            onClick={() => {
              const nextState = toggleSound()
              setSoundOn(nextState)
            }}
            title={soundOn ? "MUTE HUD AUDIO" : "UNMUTE HUD AUDIO"}
            style={{ fontSize: '14px', marginRight: '6px' }}
          >
            {soundOn ? '🔊' : '🔇'}
          </button>
          <div className="settings-container-hud">
            <button className="icon-button" onClick={() => setShowSettings(!showSettings)}>
              ⚙️
            </button>
            {showSettings && (
              <div className="settings-dropdown heavy-panel">
                <div className="rivet rivet-tl"></div>
                <div className="rivet rivet-tr"></div>
                <div className="rivet rivet-bl"></div>
                <div className="rivet rivet-br"></div>
                <div className="dropdown-header">THEME</div>
                <div className="dropdown-options">
                  <button 
                    className={`dropdown-opt-btn ${theme === 'amber' ? 'active' : ''}`}
                    onClick={() => { setTheme('amber'); setShowSettings(false); }}
                  >
                    📟 CAST_IRON_AMBER
                  </button>
                  <button 
                    className={`dropdown-opt-btn ${theme === 'green' ? 'active' : ''}`}
                    onClick={() => { setTheme('green'); setShowSettings(false); }}
                  >
                    🟢 FALLOUT_GREEN
                  </button>
                  <button 
                    className={`dropdown-opt-btn ${theme === 'steampunk' ? 'active' : ''}`}
                    onClick={() => { setTheme('steampunk'); setShowSettings(false); }}
                  >
                    🎛️ STEAMPUNK_COPPER
                  </button>
                  <button
                    className={`dropdown-opt-btn ${theme === 'phantom' ? 'active' : ''}`}
                    onClick={() => { setTheme('phantom'); setShowSettings(false); }}
                  >
                    ⬜ PHANTOM_WHITE
                  </button>
                  <button
                    className={`dropdown-opt-btn ${theme === 'archive' ? 'active' : ''}`}
                    onClick={() => { setTheme('archive'); setShowSettings(false); }}
                  >
                    📜 ARCHIVE
                  </button>
                </div>
              </div>
            )}
          </div>
          <button className="icon-button power-btn" onClick={() => { if (window.confirm('Return to player setup? Your current progress will be lost.')) { setShowCallsignScreen(true); setCallsign(''); setCallsignInput(''); } }}>
            ⏻
          </button>
        </div>
      </header>

      {/* 2. Main Body Dashboard Chassis */}
      <div className="dashboard-container">
        {/* Leftmost Vertical Strip */}
        <div className="vertical-menu">
          <div className="vertical-menu-top">
            <div className="menu-item active">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
                <circle cx="12" cy="7" r="4" />
              </svg>
              <span className="menu-item-text">{(callsign || 'OP_01').substring(0, 6).toUpperCase()}</span>
            </div>
            <div className="menu-item" onClick={() => handleCommand('status')}>
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <rect x="3" y="3" width="18" height="18" rx="2" ry="2" />
                <line x1="9" y1="9" x2="15" y2="9" />
                <line x1="9" y1="13" x2="15" y2="13" />
                <line x1="9" y1="17" x2="13" y2="17" />
              </svg>
              <span className="menu-item-text">STATS</span>
            </div>
            <div className="menu-item" onClick={() => handleCommand('inventory')}>
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z" />
              </svg>
              <span className="menu-item-text">CARGO</span>
            </div>
            <div className="menu-item" onClick={() => handleCommand('look')}>
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <circle cx="12" cy="12" r="10" />
                <line x1="22" y1="12" x2="18" y2="12" />
                <line x1="6" y1="12" x2="2" y2="12" />
                <line x1="12" y1="6" x2="12" y2="2" />
                <line x1="12" y1="22" x2="12" y2="18" />
              </svg>
              <span className="menu-item-text">SAT_VIEW</span>
            </div>
          </div>
          <div className="vertical-menu-bottom">
            <div className="menu-item" onClick={async () => {
              try {
                const res = await fetch('/leaderboard/top')
                const data = await res.json()
                setLeaderboard(data)
                setShowLeaderboard(true)
              } catch {
                setLeaderboard([])
                setShowLeaderboard(true)
              }
            }}>
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <polyline points="23 6 13.5 15.5 8.5 10.5 1 18" />
                <polyline points="17 6 23 6 23 12" />
              </svg>
              <span className="menu-item-text" style={{ fontSize: '7px' }}>RANKS</span>
            </div>
            <div className="menu-item" onClick={() => {
              if (!scoreSavedRef.current && state && (state.points > 0 || state.moveCount > 0)) {
                scoreSavedRef.current = true
                fetch('/leaderboard/save', {
                  method: 'POST',
                  headers: { 'Content-Type': 'application/json' },
                  body: JSON.stringify({
                    callsign: callsign || 'OPERATOR_01',
                    score: state.points || 0,
                    moveCount: state.moveCount || 0
                  })
                }).catch(() => {})
              }
              initGame(callsign)
            }}>
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M21.5 2v6h-6M21.34 15.57a10 10 0 1 1-.57-8.38l5.67-5.67" />
              </svg>
              <span className="menu-item-text" style={{ fontSize: '7px' }}>REBOOT</span>
            </div>
          </div>
        </div>

        {/* 3. Render content matching Active Tab */}
        {activeTab === 'MAP_INTEL' ? (
          <div className={`tactical-grid${rightPanelCollapsed ? ' right-collapsed' : ''}`}>
            {/* Left Column: Operator Sync Dossier */}
            <Sidebar
              state={state}
              onCommand={handleCommand}
              lastCommand={commandHistory[commandHistory.length - 1]}
              callsign={callsign}
            />

            {/* Center Column: Telemetry monitor and log panel */}
            <StoryPanel messages={messages} state={state} activeBg={activeBg} fadingBg={fadingBg} />

            {/* Right Column: session load, signatures inventory, active quests */}
            <RightPanel state={state} onShareSession={handleShareSession} linkCopied={linkCopied} onCommand={handleCommand} collapsed={rightPanelCollapsed} onToggleCollapse={() => setRightPanelCollapsed(c => !c)} />
          </div>
        ) : (
          /* Screen 2: Completion Debrief Screen Dashboard */
          <div className="debrief-dashboard">
            <div className="debrief-header">
              <div className="debrief-status-block">
                <div className="debrief-title">MISSION COMPLETE</div>
                <div className="debrief-meta-line">
                  ID_{((state?.points || 0) + 12).toString().padStart(3, '0')} · MOVES: {(state?.moveCount || 0)} · DEBRIEF
                </div>
              </div>

              <div className="debrief-header-actions">
                <button className="mechanical-plate active-yellow" onClick={() => initGame(callsign)}>
                  🔄 RESTART
                </button>
                <button className="mechanical-plate outline-yellow" onClick={async () => {
                  try {
                    const res = await fetch('/leaderboard/top')
                    const data = await res.json()
                    setLeaderboard(data)
                    setShowLeaderboard(true)
                  } catch {
                    setLeaderboard([])
                    setShowLeaderboard(true)
                  }
                }}>
                  GLOBAL RANKINGS
                </button>
                <button className="mechanical-plate outline-yellow" onClick={async () => {
                  try {
                    const res = await fetch('/leaderboard/top-dpu')
                    const data = await res.json()
                    setDpuLeaderboard(data)
                    setShowDpuLeaderboard(true)
                  } catch {
                    setDpuLeaderboard([])
                    setShowDpuLeaderboard(true)
                  }
                }}>
                  DEPAUW RANKINGS
                </button>
              </div>
            </div>

            <div className="debrief-grid">
              {/* Score Circular gauge */}
              <div className="debrief-card heavy-panel circular-gauge-panel">
                <div className="rivet rivet-tl"></div>
                <div className="rivet rivet-tr"></div>
                <div className="rivet rivet-bl"></div>
                <div className="rivet rivet-br"></div>
                <span className="debrief-card-title">RATING</span>

                <div className="rating-gauge-container">
                  <svg viewBox="0 0 120 120" className="circular-svg">
                    <circle cx="60" cy="60" r="50" fill="none" stroke="rgba(242, 202, 80, 0.1)" strokeWidth="8" />
                    {/* Glowing perimeter score indicator */}
                    <circle cx="60" cy="60" r="50" fill="none" stroke="var(--color-primary)" strokeWidth="8" 
                      strokeDasharray="314" strokeDashoffset={314 - (314 * Math.min(state?.points || 0, 100)) / 100}
                      className="gauge-progress-arc" />
                  </svg>
                  <div className="rating-center-txt">
                    <span className="rating-score-lbl">FINAL_SCORE</span>
                    <span className="rating-score-num">
                      {((state?.points || 0) * 100).toLocaleString()}
                    </span>
                  </div>
                </div>

                <div className="circular-stats-footer">
                  <div className="circ-stat">
                    <span className="circ-stat-lbl">TOTAL_TIME</span>
                    <span className="circ-stat-val">{formatSessionTime(sessionSecs)}</span>
                  </div>
                  <div className="circ-stat">
                    <span className="circ-stat-lbl">RANKING</span>
                    <span className="circ-stat-val val-gold">{getDebriefRanking()}</span>
                  </div>
                </div>
              </div>

              {/* Operational Efficiency metrics */}
              <div className="debrief-card heavy-panel efficiency-panel">
                <div className="rivet rivet-tl"></div>
                <div className="rivet rivet-tr"></div>
                <div className="rivet rivet-bl"></div>
                <div className="rivet rivet-br"></div>
                
                <span className="debrief-card-title">EFFICIENCY</span>
                <span className="hud-tag hud-tag-success efficiency-tag">CALIBRATED</span>

                <div className="eff-body">
                  {/* Accuracy Wave chart */}
                  <div className="efficiency-metric-row">
                    <div className="eff-metric-lbl">
                      <span>MOVE_ACCURACY</span>
                      <span className="eff-val-gold">{getMoveAccuracy()}</span>
                    </div>
                    <div className="accuracy-wave-container">
                      <svg viewBox="0 0 300 40" className="wave-svg">
                        <path d="M 0 25 Q 40 10, 80 30 T 160 20 T 240 25 L 300 15" fill="none" stroke="var(--color-primary)" strokeWidth="2" />
                      </svg>
                    </div>
                  </div>

                  {/* Quest progress segmented meter */}
                  {(() => {
                    const questsDone = [
                      state?.musicTaskComplete, state?.dnaTaskComplete,
                      state?.salmonTaskComplete, state?.snakeTaskComplete,
                      state?.macbookTaskComplete, state?.treadmillUsed,
                      state?.artifactTaskComplete
                    ].filter(Boolean).length
                    const filledSegs = Math.round((questsDone / 7) * 10)
                    const moves = state?.moveCount || 0
                    const pts = state?.points || 0
                    const ppm = moves > 0 ? pts / moves : 1
                    const effLabel = ppm >= 0.8 ? 'OPTIMAL' : ppm >= 0.4 ? 'EFFICIENT' : 'STRAINED'
                    const effColor = ppm >= 0.8 ? 'var(--color-primary)' : ppm >= 0.4 ? 'var(--color-primary)' : 'var(--color-error)'
                    const avgTime = moves > 0 ? (sessionSecs / moves).toFixed(1) : '—'
                    return (
                      <>
                        <div className="efficiency-metric-row">
                          <div className="eff-metric-lbl">
                            <span>AVG_TIME/MOVE</span>
                            <span className="eff-val-gold">{moves > 0 ? `${avgTime}S` : '—'}</span>
                          </div>
                          <div className="segmented-meter">
                            {Array.from({ length: 10 }, (_, i) => (
                              <div key={i} className={`meter-segment ${i < filledSegs ? 'active' : ''}`}></div>
                            ))}
                          </div>
                        </div>
                        <div className="dashed-list" style={{ marginTop: '10px' }}>
                          <div className="dashed-row">
                            <span style={{ color: 'var(--color-outline)' }}>EFFICIENCY</span>
                            <span style={{ color: effColor, fontWeight: 'bold' }}>{effLabel}</span>
                          </div>
                          <div className="dashed-row">
                            <span style={{ color: 'var(--color-outline)' }}>QUESTS_DONE</span>
                            <span style={{ color: questsDone >= 3 ? 'var(--color-primary)' : 'var(--color-outline)', fontWeight: 'bold' }}>{questsDone}/7</span>
                          </div>
                        </div>
                      </>
                    )
                  })()}
                </div>
              </div>

              {/* Unlocked Badges Panel */}
              <div className="debrief-card heavy-panel badges-panel">
                <div className="rivet rivet-tl"></div>
                <div className="rivet rivet-tr"></div>
                <div className="rivet rivet-bl"></div>
                <div className="rivet rivet-br"></div>
                <span className="debrief-card-title">BADGES</span>

                <div className="badges-grid-layout">
                  <div className={`badge-box ${isBadgeUnlocked('SPEED_RUNNER') ? 'unlocked' : 'locked'}`}>
                    <span className="badge-icon">⚡</span>
                    <span className="badge-lbl">SPEED_RUNNER</span>
                  </div>
                  <div className={`badge-box ${isBadgeUnlocked('CAMPUS_LEGEND') ? 'unlocked' : 'locked'}`}>
                    <span className="badge-icon">🎓</span>
                    <span className="badge-lbl">CAMPUS_LEGEND</span>
                  </div>
                  <div className={`badge-box ${isBadgeUnlocked('GHOST_OPERATOR') ? 'unlocked' : 'locked'}`}>
                    <span className="badge-icon">👁️‍🗨️</span>
                    <span className="badge-lbl">GHOST_OPERATOR</span>
                  </div>
                  <div className={`badge-box ${isBadgeUnlocked('UNTOUCHABLE') ? 'unlocked' : 'locked'}`}>
                    <span className="badge-icon">🛡️</span>
                    <span className="badge-lbl">UNTOUCHABLE</span>
                  </div>
                </div>
              </div>

              {/* Completion Log Checklists */}
              <div className="debrief-card heavy-panel checklist-panel">
                <div className="rivet rivet-tl"></div>
                <div className="rivet rivet-tr"></div>
                <div className="rivet rivet-bl"></div>
                <div className="rivet rivet-br"></div>
                <span className="debrief-card-title">QUESTS</span>

                <div className="checklist-body scrollable-checklist">
                  <div className="checklist-item checked">
                    <span className="check-box">✓</span>
                    <span>CAMPUS: ADVENTURE STARTED AT EAST COLLEGE</span>
                  </div>
                  <div className={`checklist-item ${state?.sashaCalled ? 'checked' : 'unchecked'}`}>
                    <span className="check-box">{state?.sashaCalled ? '✓' : '✗'}</span>
                    <span>MUSIC: SASHA CONTACTED VIA PHONE</span>
                  </div>
                  <div className={`checklist-item ${state?.musicTaskComplete ? 'checked' : 'unchecked'}`}>
                    <span className="check-box">{state?.musicTaskComplete ? '✓' : '✗'}</span>
                    <span>MUSIC: GCPA SHOW COMPLETE</span>
                  </div>
                  <div className={`checklist-item ${state?.dnaTaskComplete ? 'checked' : 'unchecked'}`}>
                    <span className="check-box">{state?.dnaTaskComplete ? '✓' : '✗'}</span>
                    <span>DNA: BATMAN SAMPLE DELIVERED TO JULIAN</span>
                  </div>
                  <div className={`checklist-item ${state?.salmonTaskComplete ? 'checked' : 'unchecked'}`}>
                    <span className="check-box">{state?.salmonTaskComplete ? '✓' : '✗'}</span>
                    <span>WILDLIFE: SALMON SAVED (OLIN AQUARIUM)</span>
                  </div>
                  <div className={`checklist-item ${state?.snakeTaskComplete ? 'checked' : 'unchecked'}`}>
                    <span className="check-box">{state?.snakeTaskComplete ? '✓' : '✗'}</span>
                    <span>WILDLIFE: SNAKES SECURED (OLIN SAFEBOX)</span>
                  </div>
                  <div className={`checklist-item ${state?.macbookTaskComplete ? 'checked' : 'unchecked'}`}>
                    <span className="check-box">{state?.macbookTaskComplete ? '✓' : '✗'}</span>
                    <span>MACBOOK: RETURNED TO ADMIN BUILDING</span>
                  </div>
                  <div className={`checklist-item ${state?.treadmillUsed ? 'checked' : 'unchecked'}`}>
                    <span className="check-box">{state?.treadmillUsed ? '✓' : '✗'}</span>
                    <span>FITNESS: TREADMILL CHALLENGE COMPLETE</span>
                  </div>
                  <div className={`checklist-item ${state?.artifactTaskComplete ? 'checked' : 'unchecked'}`}>
                    <span className="check-box">{state?.artifactTaskComplete ? '✓' : '✗'}</span>
                    <span>ARTIFACT: ANCIENT ARTIFACT RETURNED TO EAST COLLEGE</span>
                  </div>
                  <div className={`checklist-item ${state?.stadiumTaskComplete ? 'checked' : 'unchecked'}`}>
                    <span className="check-box">{state?.stadiumTaskComplete ? '✓' : '✗'}</span>
                    <span>BONUS: STADIUM SPRINT — MONON BELL MOMENT</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>

      {/* 4. Command input area (only rendered in gameplay map tab) */}
      {activeTab === 'MAP_INTEL' && (
        <CommandInput
          getHistoryCommand={getHistoryCommand}
          handleHistoryNav={handleHistoryNav}
          onCommand={handleCommand}
          state={state}
          pending={commandPending}
          promptError={promptError}
        />
      )}

      {/* Projected Glass HUD Help Modal Overlay */}
      {showHelpModal && (
        <div className="hud-modal-overlay">
          <div className="hud-modal-card heavy-panel">
            <div className="rivet rivet-tl"></div>
            <div className="rivet rivet-tr"></div>
            <div className="rivet rivet-bl"></div>
            <div className="rivet rivet-br"></div>
            
            <div className="panel-header modal-header">
              <span className="panel-title">MANUAL</span>
              <button className="mechanical-plate active-yellow modal-close-btn" onClick={() => setShowHelpModal(false)}>
                ✕ CLOSE_DECK
              </button>
            </div>
            
            <div className="panel-body modal-body-deck">
              <div className="modal-section">
                <div className="modal-sec-header">🎮 CORE OBJECTIVE</div>
                <div className="modal-sec-content">
                  DePauw needs you. Endangered animals are at risk. A legendary musician's lost relics are scattered across campus.
                  The President's MacBook has vanished. A sealed box of venomous snakes sits unnoticed at The Fluttering Duck —
                  one wrong move by the wrong person and it's over. An ancient artifact sits in the wrong building, waiting to be returned.
                  And for those who can push past their limits, the Monon Bell and a legendary stadium moment await.
                  <br/><br/>
                  Navigate campus, complete your missions, and protect what matters. Move efficiently — every wasted step costs you.
                  Hunger builds as you travel, so eat when you can. The most thorough explorers find things others miss.
                  <br/><br/>
                  <strong>Starting point:</strong> East College. Seven core quests. One bonus sprint. Make it count.
                </div>
              </div>

              <div className="modal-section">
                <div className="modal-sec-header">🖥️ TERMINAL CONTROLS</div>
                <div className="modal-sec-grid">
                  <div><strong>HELP</strong></div>
                  <div>Opens this manual at any time.</div>

                  <div><strong>LOOK</strong></div>
                  <div>Redisplays your current location description and visible items.</div>

                  <div><strong>STATUS</strong></div>
                  <div>Shows your current score, move count, and quest progress.</div>

                  <div><strong>QUESTS</strong></div>
                  <div>Lists all active quests and their current completion state.</div>

                  <div><strong>EXITS</strong></div>
                  <div>Lists all available exits from your current location with destination names (e.g. north → GCPA).</div>

                  <div><strong>CLEAR</strong></div>
                  <div>Wipes the terminal log. Progress, score, and inventory are untouched.</div>

                  <div><strong>QUIT</strong></div>
                  <div>Ends your run, saves your score to the leaderboard, and shows your final stats. Use the <strong>GLOBAL RANKINGS</strong> button on the debrief screen to see where you placed.</div>
                </div>
              </div>

              <div className="modal-section">
                <div className="modal-sec-header">🧭 MOVEMENT CONTROLS</div>
                <div className="modal-sec-grid">
                  <div><strong>GO &lt;direction&gt;</strong></div>
                  <div>Cardinal direction (north, south, east, west) to move. Costs 1 move.</div>

                  <div><strong>JUMP</strong></div>
                  <div>Special teleport action at key campus spots. Costs 2 moves.</div>

                  <div><strong>CROSS</strong></div>
                  <div>Bridge crossing action at central campus hubs. Costs 2 moves.</div>
                </div>
              </div>

              <div className="modal-section">
                <div className="modal-sec-header">📦 INVENTORY CONTROLS</div>
                <div className="modal-sec-grid">
                  <div><strong>INVENTORY</strong></div>
                  <div>Lists everything currently in your cargo.</div>

                  <div><strong>TAKE &lt;item&gt;</strong></div>
                  <div>Pick up items at your current location and add to cargo.</div>

                  <div><strong>DROP &lt;item&gt;</strong></div>
                  <div>Remove an item from cargo and leave it at your location.</div>

                  <div><strong>EXAMINE &lt;item&gt;</strong></div>
                  <div>Get full details on any item in your cargo or at your location.</div>

                  <div><strong>OPEN &lt;container&gt;</strong></div>
                  <div>Inspect a container at your current location to see what's inside.</div>

                  <div><strong>PUT &lt;item&gt; IN &lt;container&gt;</strong></div>
                  <div>Place a cargo item inside a container (e.g. put Salmon in Aquarium).</div>

                  <div><strong>USE &lt;item&gt;</strong></div>
                  <div>Interact with special objects — e.g. <strong>use treadmill</strong> at Lilly to start the sprint challenge. Also works on food items to eat them.</div>
                </div>
              </div>

              <div className="modal-section">
                <div className="modal-sec-header">🧬 ACTIVE PRIORITY RUNS</div>
                <div className="modal-sec-grid">
                  <div><strong>The Music Show</strong></div>
                  <div>Call Sasha via Phone. Collect microphone & glowing guitar, combine sheets, and secure all in GCPA GuitarCase.</div>

                  <div><strong>DNA Delivery</strong></div>
                  <div>Grab PCR DNA from Olin and rush to Julian within 3 moves before it spoils!</div>

                  <div><strong>Endangered Salmon</strong></div>
                  <div>Save the dying salmon at Hoover and place it inside Olin's Aquarium.</div>

                  <div><strong>Treasure Box (Snakes)</strong></div>
                  <div>Carry sealed TreasureBox from The Fluttering Duck to Olin, then place inside SafeBox. Do NOT open the box!</div>

                  <div><strong>MacBook Return</strong></div>
                  <div>The President's Macbook was spotted at Julian. Return it to the MacbookCase at the Administration Building.</div>

                  <div><strong>Treadmill Challenge</strong></div>
                  <div>Head to Lilly Building and type <strong>use treadmill</strong> to start the sprint. Ace the typing challenge for a points boost.</div>

                  <div><strong>Ancient Artifact</strong></div>
                  <div>The AncientArtifact in Julian's DisplayCase doesn't belong there. Return it to the HistoryWall at East College.</div>

                  <div><strong>Stadium Sprint</strong> ⚡ BONUS</div>
                  <div>Unlocks after completing the treadmill. Accept the coach's offer and ace a second typing challenge to be transported to the Stadium for a legendary Monon Bell moment. +25 pts.</div>
                </div>
              </div>

              <div className="modal-section">
                <div className="modal-sec-header">⚠️ DINING PENALTY</div>
                <div className="modal-sec-grid">
                  <div><strong>First dining hall</strong></div>
                  <div>Free — visit Hoover or The Fluttering Duck with no consequence.</div>
                  <div><strong>Second dining hall</strong></div>
                  <div>Penalty: <strong>+10 moves added, −25 points lost.</strong> Choose your spot wisely.</div>
                  <div><strong>Food limit</strong></div>
                  <div>Carry max 3 foods. They spoil after a few moves if not eaten.</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Leaderboard Modal */}
      {showLeaderboard && (
        <div className="hud-modal-overlay" onClick={() => setShowLeaderboard(false)}>
          <div className="hud-modal-card heavy-panel" onClick={e => e.stopPropagation()} style={{ width: '480px' }}>
            <div className="rivet rivet-tl"></div>
            <div className="rivet rivet-tr"></div>
            <div className="rivet rivet-bl"></div>
            <div className="rivet rivet-br"></div>
            <div className="modal-header">
              <span className="panel-title">GLOBAL RANKINGS</span>
              <button className="mechanical-plate modal-close-btn" onClick={() => setShowLeaderboard(false)}>CLOSE</button>
            </div>
            <div className="modal-body-deck">
              {leaderboard && leaderboard.length > 0 ? (
                <table style={{ width: '100%', borderCollapse: 'collapse', fontFamily: 'var(--font-label)', fontSize: '11px' }}>
                  <thead>
                    <tr style={{ color: 'var(--color-outline)', borderBottom: '1px solid var(--color-surface-bright)' }}>
                      <th style={{ padding: '6px 8px', textAlign: 'left' }}>#</th>
                      <th style={{ padding: '6px 8px', textAlign: 'left' }}>CALLSIGN</th>
                      <th style={{ padding: '6px 8px', textAlign: 'right' }}>SCORE</th>
                      <th style={{ padding: '6px 8px', textAlign: 'right' }}>MOVES</th>
                    </tr>
                  </thead>
                  <tbody>
                    {leaderboard.map((entry, i) => (
                      <tr key={entry.id} style={{ borderBottom: '1px dashed var(--color-surface-bright)', color: i === 0 ? 'var(--color-primary)' : 'var(--color-on-surface)' }}>
                        <td style={{ padding: '8px 8px' }}>{i + 1}</td>
                        <td style={{ padding: '8px 8px', fontWeight: 700 }}>{entry.callsign}</td>
                        <td style={{ padding: '8px 8px', textAlign: 'right' }}>{entry.score}</td>
                        <td style={{ padding: '8px 8px', textAlign: 'right' }}>{entry.moveCount}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              ) : (
                <div style={{ textAlign: 'center', padding: '24px', color: 'var(--color-outline)', fontFamily: 'var(--font-label)', fontSize: '11px' }}>
                  NO SCORES ON RECORD YET. COMPLETE A MISSION TO APPEAR HERE.
                </div>
              )}
            </div>
          </div>
        </div>
      )}
      {/* Typing Challenge Overlay — shown when penalty challenge is active */}
      {state?.typingChallengeActive && state?.typingWords?.length > 0 && (
        <div className="hud-modal-overlay">
          <div className="hud-modal-card heavy-panel" style={{ width: '480px' }}>
            <div className="rivet rivet-tl"></div>
            <div className="rivet rivet-tr"></div>
            <div className="rivet rivet-bl"></div>
            <div className="rivet rivet-br"></div>

            <div className="panel-header modal-header">
              <span className="panel-title">⌨ INPUT_VERIFICATION</span>
              <span className="hud-tag hud-tag-error">CHALLENGE_ACTIVE</span>
            </div>

            <div className="modal-body-deck">
              <div className="modal-section">
                <div className="modal-sec-header">REQUIRED WORDS</div>
                <div className="modal-sec-content" style={{ marginBottom: '10px' }}>
                  Type at least <strong>3 of these words</strong> in one message below to continue:
                </div>
                <div className="typing-challenge-words">
                  {state.typingWords.map((word, i) => (
                    <span key={i} className="typing-word-chip">{word}</span>
                  ))}
                </div>
              </div>
              <div className="modal-section">
                <div className="modal-sec-content" style={{ textAlign: 'center', color: 'var(--color-outline)', fontSize: '11px' }}>
                  Type them in one line in the terminal — or type <strong style={{ color: 'var(--color-primary)' }}>skip</strong> to dismiss the penalty.
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* DePauw Leaderboard Modal */}
      {showDpuLeaderboard && (
        <div className="hud-modal-overlay" onClick={() => setShowDpuLeaderboard(false)}>
          <div className="hud-modal-card heavy-panel" onClick={e => e.stopPropagation()} style={{ width: '480px' }}>
            <div className="rivet rivet-tl"></div>
            <div className="rivet rivet-tr"></div>
            <div className="rivet rivet-bl"></div>
            <div className="rivet rivet-br"></div>
            <div className="modal-header">
              <span className="panel-title">DEPAUW RANKINGS</span>
              <button className="mechanical-plate modal-close-btn" onClick={() => setShowDpuLeaderboard(false)}>CLOSE</button>
            </div>
            <div className="modal-body-deck">
              {dpuLeaderboard && dpuLeaderboard.length > 0 ? (
                <table style={{ width: '100%', borderCollapse: 'collapse', fontFamily: 'var(--font-label)', fontSize: '11px' }}>
                  <thead>
                    <tr style={{ color: 'var(--color-outline)', borderBottom: '1px solid var(--color-surface-bright)' }}>
                      <th style={{ padding: '6px 8px', textAlign: 'left' }}>#</th>
                      <th style={{ padding: '6px 8px', textAlign: 'left' }}>CALLSIGN</th>
                      <th style={{ padding: '6px 8px', textAlign: 'right' }}>SCORE</th>
                      <th style={{ padding: '6px 8px', textAlign: 'right' }}>MOVES</th>
                    </tr>
                  </thead>
                  <tbody>
                    {dpuLeaderboard.map((entry, i) => (
                      <tr key={entry.id} style={{ borderBottom: '1px dashed var(--color-surface-bright)', color: i === 0 ? 'var(--color-primary)' : 'var(--color-on-surface)' }}>
                        <td style={{ padding: '8px 8px' }}>{i + 1}</td>
                        <td style={{ padding: '8px 8px', fontWeight: 700 }}>{entry.callsign}</td>
                        <td style={{ padding: '8px 8px', textAlign: 'right' }}>{entry.score}</td>
                        <td style={{ padding: '8px 8px', textAlign: 'right' }}>{entry.moveCount}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              ) : (
                <div style={{ textAlign: 'center', padding: '24px', color: 'var(--color-outline)', fontFamily: 'var(--font-label)', fontSize: '11px' }}>
                  NO DEPAUW SCORES YET. ADD _dpu TO YOUR NAME TO APPEAR HERE.
                </div>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
