import { useState, useRef, useEffect } from 'react'
import './CommandInput.css'

export default function CommandInput({ getHistoryCommand, handleHistoryNav, onCommand }) {
  const [input, setInput] = useState('')
  const inputRef = useRef(null)

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

  const handleKeyDown = (e) => {
    if (e.key === 'Enter') {
      e.preventDefault()
      handleSubmit()
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

  return (
    <div className="command-input-area">
      <div className="input-section">
        <span className="prompt">></span>
        <input
          ref={inputRef}
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyDown={handleKeyDown}
          placeholder="Type your command here..."
          autoFocus
        />
        <button onClick={handleSubmit}>Send</button>
      </div>
      <div className="hints">
        💡 Tip: Use arrow keys for command history | Type 'HELP' for commands
      </div>
    </div>
  )
}
