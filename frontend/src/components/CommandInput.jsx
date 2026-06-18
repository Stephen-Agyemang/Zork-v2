import { useState, useRef, useEffect } from 'react'
import { playTick, playBeep } from '../utils/audio'
import './CommandInput.css'

export default function CommandInput({ getHistoryCommand, handleHistoryNav, onCommand, state, promptError, pending }) {
  const [input, setInput] = useState('')
  const inputRef = useRef(null)

  // Focus on mount and whenever pending clears (after each response)
  useEffect(() => {
    if (!pending) inputRef.current?.focus()
  }, [pending])

  const handleSubmit = () => {
    const cmd = input.trim()
    if (cmd && !pending) {
      onCommand(cmd)
      setInput('')
      inputRef.current?.focus()
    }
  }

  const allCommands = [
    'look', 
    'go north', 'go south', 'go east', 'go west', 
    'connections', 'inventory', 'status', 'quests',
    'examine help', 'use treadmill', 'clear', 'quit'
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
    { label: 'GO SOUTH', cmd: 'go south' },
    { label: 'INVENTORY', cmd: 'inventory' },
    { label: 'EXITS', cmd: 'connections' },
  ]

  const handleMacroClick = (cmd) => {
    if (pending) return
    playBeep()
    onCommand(cmd)
    inputRef.current?.focus()
  }

  // Detect typing challenge active to color-code input glow
  const isChallenge = state?.typingChallengeActive

  return (
    <div className={`command-input-area heavy-panel ${isChallenge ? 'typing-challenge-active' : ''} ${pending ? 'command-pending' : ''}`}>
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

        <div className="deck-input-wrapper">
          {ghostText && (
            <span className="deck-ghost-text">{ghostText}</span>
          )}
          <input
            ref={inputRef}
            value={input}
            onChange={(e) => { if (!pending) { setInput(e.target.value); playTick() } }}
            onKeyDown={handleKeyDown}
            placeholder={pending ? "PROCESSING..." : isChallenge ? "TYPE THE WORDS SHOWN ABOVE..." : "INITIATE COMMAND SEQUENCE..."}
            disabled={pending}
            autoFocus
            className="deck-input-field"
          />
        </div>
      </div>

      {/* Macros + centered Execute */}
      <div className="deck-macros-row">
        {macros.slice(0, 2).map((m, idx) => (
          <button key={idx} className="macro-chip-btn" onClick={() => handleMacroClick(m.cmd)}>
            {m.label}
          </button>
        ))}
        <button className="deck-execute-btn" onClick={handleSubmit}>EXECUTE</button>
        {macros.slice(2).map((m, idx) => (
          <button key={idx} className="macro-chip-btn" onClick={() => handleMacroClick(m.cmd)}>
            {m.label}
          </button>
        ))}
      </div>
    </div>
  )
}
