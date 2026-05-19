import './RightPanel.css'

export default function RightPanel({ state }) {
  return (
    <div className="right-panel">
      <div className="section">
        <h3>QUICK STATS</h3>
        {state && (
          <>
            <div className="stat">
              <span className="label">Score:</span>
              <span className="value">{state.points || 0}</span>
            </div>
            <div className="stat">
              <span className="label">Moves:</span>
              <span className="value">{state.moveCount || 0}</span>
            </div>
            <div className="stat">
              <span className="label">Hunger:</span>
              <span className="bar">▓▓▓░░░░░░</span>
            </div>
            {state.dnaTaskActive && state.dnaMovesLeft >= 0 && (
              <div className="stat dna-alert">
                <span className="label">DNA:</span>
                <span className="value">⚠️ {state.dnaMovesLeft}m</span>
              </div>
            )}
          </>
        )}
      </div>

      <div className="section">
        <h3>LOCATION</h3>
        {state?.currLocation && (
          <>
            <div className="location-name">{state.currLocation.name}</div>
            <div className="location-desc">{state.currLocation.description}</div>
          </>
        )}
      </div>

      <div className="section">
        <h3>INVENTORY</h3>
        {state?.inventory?.items && state.inventory.items.length > 0 ? (
          <div className="inventory-list">
            {state.inventory.items.map((item, idx) => (
              <div key={idx} className="item">
                {idx + 1}. {item.name}
              </div>
            ))}
          </div>
        ) : (
          <div className="empty">Empty</div>
        )}
      </div>
    </div>
  )
}
