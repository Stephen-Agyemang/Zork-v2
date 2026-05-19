import { useState, useEffect, useRef } from 'react'
import Sidebar from './components/Sidebar'
import StoryPanel from './components/StoryPanel'
import RightPanel from './components/RightPanel'
import CommandInput from './components/CommandInput'

export default function App() {
  const [state, setState] = useState(null)
  const [messages, setMessages] = useState([])
  const [commandHistory, setCommandHistory] = useState([])
  const [historyIndex, setHistoryIndex] = useState(-1)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const pollIntervalRef = useRef(null)

  useEffect(() => {
    initGame()
    return () => clearInterval(pollIntervalRef.current)
  }, [])

  const initGame = async () => {
    try {
      await fetch('/game/start', { method: 'POST' })
      await fetchState()
      setMessages([{ type: 'system', text: 'Welcome to ZORK v2! Type commands below.' }])
      setLoading(false)

      pollIntervalRef.current = setInterval(fetchState, 2000)
    } catch (e) {
      setError(e.message)
      setLoading(false)
    }
  }

  const fetchState = async () => {
    try {
      const response = await fetch('/game/state')
      if (!response.ok) throw new Error('Failed to fetch state')
      const data = await response.json()
      setState(data)
    } catch (e) {
      console.error('State fetch error:', e)
    }
  }

  const handleCommand = async (command) => {
    if (!command.trim()) return

    try {
      setCommandHistory(prev => [...prev, command])
      setHistoryIndex(-1)
      setMessages(prev => [...prev, { type: 'command', text: `> ${command}` }])

      const response = await fetch('/game/command', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ command })
      })

      const output = await response.text()
      setMessages(prev => [...prev, { type: 'output', text: output }])
      await fetchState()
    } catch (e) {
      setMessages(prev => [...prev, { type: 'error', text: `Error: ${e.message}` }])
    }
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

  if (loading) {
    return <div className="loading">Loading game...</div>
  }

  if (error) {
    return <div className="error">Error: {error}</div>
  }

  return (
    <div className="container">
      <header>
        <h1>ZORK v2</h1>
        {state && (
          <div className="header-stats">
            <span>{state.currLocation?.name || 'Unknown'}</span>
            <span>Score: {state.points || 0}</span>
            <span>Moves: {state.moveCount || 0}</span>
            {state.dnaTaskActive && state.dnaMovesLeft >= 0 && (
              <span className="dna-alert">DNA: ⚠️ {state.dnaMovesLeft}</span>
            )}
          </div>
        )}
      </header>

      <div className="main-grid">
        <Sidebar state={state} />
        <StoryPanel messages={messages} />
        <RightPanel state={state} />
      </div>

      <CommandInput
        getHistoryCommand={getHistoryCommand}
        handleHistoryNav={handleHistoryNav}
        onCommand={handleCommand}
      />
    </div>
  )
}
