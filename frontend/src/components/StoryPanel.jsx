import { useEffect, useRef } from 'react'
import './StoryPanel.css'

export default function StoryPanel({ messages }) {
  const messagesEndRef = useRef(null)

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
  }

  useEffect(() => {
    scrollToBottom()
  }, [messages])

  return (
    <div className="story-panel">
      <h3>Story & Output</h3>
      <div className="messages">
        {messages.map((msg, idx) => (
          <div key={idx} className={`message ${msg.type}`}>
            {msg.text}
          </div>
        ))}
        <div ref={messagesEndRef} />
      </div>
    </div>
  )
}
