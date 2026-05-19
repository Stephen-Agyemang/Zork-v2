import './Sidebar.css'

export default function Sidebar({ state }) {
  return (
    <div className="sidebar">
      <div className="section">
        <h3>STATUS</h3>
        {state && (
          <>
            <div className="status-item">
              <span className="label">Moves:</span>
              <span className="value">{state.moveCount || 0}</span>
            </div>
            <div className="status-item">
              <span className="label">Points:</span>
              <span className="value">{state.points || 0}</span>
            </div>
            <div className="status-item">
              <span className="label">Location:</span>
              <span className="value">{state.currLocation?.name || 'Unknown'}</span>
            </div>
          </>
        )}
      </div>

      <div className="section">
        <h3>COMMANDS</h3>
        <div className="command-list">
          <div className="cmd">
            <strong>LOOK</strong>
            <span>Shows current location</span>
          </div>
          <div className="cmd">
            <strong>EXAMINE ITEM</strong>
            <span>Inspect details</span>
          </div>
          <div className="cmd">
            <strong>TAKE ITEM</strong>
            <span>Pick up</span>
          </div>
          <div className="cmd">
            <strong>DROP ITEM</strong>
            <span>Leave behind</span>
          </div>
        </div>
      </div>

      <div className="section">
        <h3>MOVEMENT</h3>
        <div className="cmd">
          <strong>GO NORTH/SOUTH/EAST/WEST</strong> (1 move)
          <br />
          <strong>JUMP</strong> (2 moves)
          <br />
          <strong>CROSS</strong> (2 moves)
        </div>
      </div>

      <div className="section">
        <h3>TIPS</h3>
        <div className="tips">
          • Plan routes carefully
          <br />
          • Manage hunger & food
          <br />
          • DNA timer critical
          <br />
          • Type 'HELP' for more
        </div>
      </div>
    </div>
  )
}
