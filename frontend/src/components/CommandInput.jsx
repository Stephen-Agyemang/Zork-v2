import { useState, useRef, useEffect } from 'react'
import { playTick, playBeep } from '../utils/audio'
import './CommandInput.css'

export default function CommandInput({ getHistoryCommand, handleHistoryNav, onCommand, state, promptError }) {
  const [input, setInput] = useState('')
  const inputRef = useRef(null)

  // Focus the tactical input deck on start
  useEffect(() => {
    inputRef.current?.focus()
  }, [])

  const handleSubmit = () => {
    const cmd = input.trim()
    if (cmd) {
      onCommand(cmd)
      setInput('')
    }
  }

  const allCommands = [
    'look', 
    'go north', 'go south', 'go east', 'go west', 
    'connections', 'inventory', 'status', 'quests',
    'examine help', 'use treadmill', 'quit'
  ]

  // Filter commands to find matches
  const suggestions = input.trim() && !state?.typingChallengeActive
    ? allCommands.filter(c => c.startsWith(input.toLowerCase()) && c !== input.toLowerCase())
    : []

  // Create low-opacity inline autocomplete suggestion string
  const firstMatch = suggestions[0] || ''
  const ghostText = firstMatch && firstMatch.startsWith(input.toLowerCase())
    ? input + firstMatch.slice(input.length)
    : ''

  const handleKeyDown = (e) => {
    if (e.key === 'Enter') {
      e.preventDefault()
      handleSubmit()
    } else if (e.key === 'Tab') {
      // Autocomplete suggestion on Tab
      if (suggestions.length > 0) {
        e.preventDefault()
        setInput(suggestions[0])
      }
    } else if (e.key === 'ArrowRight') {
      // Autocomplete suggestion on Right Arrow key if caret is at the end of input
      const caretPos = e.target.selectionStart
      if (caretPos === input.length && suggestions.length > 0) {
        e.preventDefault()
        setInput(suggestions[0])
      }
    } else if (e.key === 'ArrowUp') {
      e.preventDefault()
      handleHistoryNav('up')
      setInput(getHistoryCommand())
    } else if (e.key === 'ArrowDown') {
      e.preventDefault()
      handleHistoryNav('down')
      setInput(getHistoryCommand())
    }
  }

  // Quick micro macro key commands
  const macros = [
    { label: 'LOOK', cmd: 'look' },
    { label: 'GO NORTH', cmd: 'go north' },
    { label: 'INVENTORY', cmd: 'inventory' },
    { label: 'EXITS', cmd: 'connections' },
  ]

  const handleMacroClick = (cmd) => {
    playBeep()
    onCommand(cmd)
    inputRef.current?.focus()
  }

  // Detect typing challenge active to color-code input glow
  const isChallenge = state?.typingChallengeActive

  return (
    <div className={`command-input-area heavy-panel ${isChallenge ? 'typing-challenge-active' : ''}`}>
      {/* Heavy Panel Rivets */}
      <div className="rivet rivet-tl"></div>
      <div className="rivet rivet-tr"></div>
      <div className="rivet rivet-bl"></div>
      <div className="rivet rivet-br"></div>

      {/* Floating Tactical Suggestion Bar */}
      {suggestions.length > 0 && (
        <div className="input-suggestions-bar">
          <span className="suggestion-label">SUGGEST:</span>
          {suggestions.slice(0, 3).map((s, i) => (
            <button 
              key={i} 
              className="suggestion-chip"
              onClick={() => {
                playTick()
                setInput(s)
                inputRef.current?.focus()
              }}
            >
              {s.toUpperCase()} <span className="suggestion-kbd">TAB</span>
            </button>
          ))}
        </div>
      )}

      <div className="input-deck-main-row">
        <span className={`deck-prompt-arrow ${promptError ? 'prompt-arrow-error' : ''}`}>&gt;</span>
        
        {/* Relative wrapper holding both transparent real input and ghost suggestion text behind it */}
        <div className="deck-input-wrapper">
          {ghostText && (
            <span className="deck-ghost-text">
              {ghostText}
            </span>
          )}
          <input
            ref={inputRef}
            value={input}
            onChange={(e) => {
              setInput(e.target.value)
              playTick()
            }}
            onKeyDown={handleKeyDown}
            placeholder={isChallenge ? "TYPE THE WORDS SHOWN IN THE LOG..." : "INITIATE COMMAND SEQUENCE..."}
            autoFocus
            className="deck-input-field"
          />
        </div>

        <button className="deck-execute-btn" onClick={handleSubmit}>
          EXECUTE
        </button>
      </div>

      {/* Quick macro action keys */}
      <div className="deck-macros-row">
        {macros.map((m, idx) => (
          <button 
            key={idx} 
            className="macro-chip-btn"
            onClick={() => handleMacroClick(m.cmd)}
          >
            {m.label}
          </button>
        ))}
      </div>
    </div>
  )
}
