import { useEffect, useRef } from 'react'
import './StoryPanel.css'

export default function StoryPanel({ messages, state, activeBg, fadingBg }) {
  const scrollRef = useRef(null)

  useEffect(() => {
    if (scrollRef.current) {
      scrollRef.current.scrollTop = scrollRef.current.scrollHeight
    }
  }, [messages])

  // Get current location telemetry formatted nicely for a compact terminal header
  const getTerminalHeaderDetails = () => {
    if (!state?.currLocation?.name) return 'SECTOR: CORE_OS // LAT: 39.6416 LON: 86.8643'
    const offset = (state.currLocation.name.length % 10) * 0.0012
    const lat = (39.6416 + offset).toFixed(4)
    const lon = (86.8643 - offset).toFixed(4)
    return `SECTOR: ${state.currLocation.name.toUpperCase().replace(/[^A-Z0-9]/g, '_')} // COORDS: ${lat}°N, ${Math.abs(lon)}°W`
  }

  return (
    <div className="center-deck">
      {/* Scrollable Story Console is now 100% of the height - the absolute Hero of the HUD! */}
      <div className="story-console-panel heavy-panel console-hero">
        {/* Structural Chassis Rivets */}
        <div className="rivet rivet-tl"></div>
        <div className="rivet rivet-tr"></div>
        <div className="rivet rivet-bl"></div>
        <div className="rivet rivet-br"></div>

        <div className="panel-header console-hero-header">
          <div className="console-header-left">
            <span className="panel-title">TACTICAL_LOG_SYSTEM</span>
            <span className="telemetry-compact-line">{getTerminalHeaderDetails()}</span>
          </div>
          
          <div className="console-header-right">
            {/* Displaying large glowing harvested score directly in terminal header */}
            <div className="compact-harvest-stat">
              <span className="compact-harvest-lbl">DATA_HARVESTED:</span>
              <span className="compact-harvest-val">
                {((state?.points || 0) * 100).toLocaleString().padStart(6, '0')}
              </span>
            </div>
            <span className="hud-tag hud-tag-primary">NEURAL_LINK: SYNC</span>
          </div>
        </div>

        {fadingBg && <div className="terminal-watermark terminal-watermark-out" style={{ backgroundImage: `url(${fadingBg})` }} />}
        {activeBg && <div className="terminal-watermark terminal-watermark-in" style={{ backgroundImage: `url(${activeBg})` }} />}

        <div className="panel-body console-log-body" ref={scrollRef}>
          
          
          {messages.map((msg, index) => {
            const secondsOffset = index * 4
            const timeStr = `10:${(4 + Math.floor(secondsOffset / 60)).toString().padStart(2, '0')}:${(secondsOffset % 60).toString().padStart(2, '0')}`

            if (msg.type === 'command') {
              return (
                <div key={index} className="log-line command-log">
                  <span className="log-time">{timeStr}</span>
                  <span className="log-content-wrapper command-text">
                    {msg.text}
                  </span>
                </div>
              )
            }

            if (msg.type === 'error') {
              return (
                <div key={index} className="log-line error-log">
                  <span className="log-time">{timeStr}</span>
                  <span className="log-content-wrapper error-text">
                    ⚠️ {msg.text}
                  </span>
                </div>
              )
            }

            return (
              <div key={index} className="log-line system-log">
                <span className="log-time">{timeStr}</span>
                <span className="log-content-wrapper system-text">
                  {msg.text}
                </span>
              </div>
            )
          })}
          
          {/* Active blinking prompt cursor */}
          <div className="log-line prompt-cursor-line">
            <span className="log-time">10:05:00</span>
            <span className="log-content-wrapper">
              <span className="prompt-arrow">&gt;</span>
              <span className="blinking-cursor"></span>
            </span>
          </div>
        </div>
      </div>
    </div>
  )
}
