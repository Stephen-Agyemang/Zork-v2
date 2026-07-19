# Zork v2

Zork v2 is a full-stack, campus-themed text adventure game set at DePauw University. A Spring Boot REST API drives the game world and session state; a React/Vite terminal-style frontend renders the experience with selectable visual themes, a callsign/profile flow, and global + campus-specific leaderboards.

> **Project status:** This is an independent, noncommercial student project created to explore Java, full-stack development, and the software development lifecycle. It began as class work and continued as a personal learning project inspired by classic parser-based interactive fiction. It is not affiliated with or endorsed by DePauw University, Microsoft, Activision, or the original creators of Zork.

## Features

- **15 campus locations** — East College (start), Julian, Olin, GCPA, Roy Library, CDI, Lilly Building, The Union Building, Hoover, The Fluttering Duck, Administration Building, Mason Hall, Reese Hall, Humbert Hall, and Stadium
- **7 core quests + 1 bonus** — DNA delivery (timed, 3-move limit), music show assembly, salmon rescue, snake containment, MacBook recovery, ancient artifact return, treadmill sprint, and the unlockable Stadium/Monon Bell bonus
- Cardinal movement (`go north/south/east/west`, 1 move) plus `jump` and `cross` (2 moves each)
- Exits display destinations on every move and `look` (`north → GCPA | west → Roy Library`)
- Inventory, container, and item interactions — `take`, `drop`, `examine`, `open`, `put`, `use`
- Hunger/food system, DNA delivery countdown, typing-challenge minigame
- Session-isolated concurrent games — many players can run simultaneously on one server
- Persistent leaderboard: global rankings + a DePauw-only leaderboard (callsigns ending in `_dpu` or `.dpu`)
- Score deduplication — one entry per callsign, only updated when a new run beats the existing best
- Profanity filtering on callsign submissions
- Live RANK label in sidebar (NOVICE → WANDERER → ADVENTURER → EXPLORER → LEGEND)
- RANKS button accessible at any point during gameplay (no need to quit first)
- Score saved on `quit`, on all-quests completion, and on REBOOT
- 5 selectable terminal UI themes (amber, green, phantom, steampunk, archive)

## Tech Stack

- **Backend:** Java 21, Spring Boot 3, Spring Data JPA
- **Database:** PostgreSQL in production (Render), H2 in-memory fallback for local development
- **Frontend:** React + Vite
- **Build/Deploy:** Maven, Docker (multi-stage build), deployed to Render

## Getting Started

### Backend

```bash
cd myproject
mvn spring-boot:run
```

The API runs on `http://localhost:8080`.

### Frontend (dev mode)

```bash
cd frontend
npm install
npm run dev
```

Vite proxies `/game` and `/leaderboard` requests to the backend on port 8080.

### Quick CLI playthrough

A simple shell client is included for testing without the frontend:

```bash
./game.sh
```

### Manual API walkthrough

```bash
# Start a session
curl -X POST http://localhost:8080/game/start \
  -H "Content-Type: application/json" \
  -d '{"callsign":"OPERATOR_01"}'
# -> { "sessionId": "...", "message": "..." }

# Send a command (sessionId from above, required on every call)
curl -X POST http://localhost:8080/game/command \
  -H "Content-Type: text/plain" \
  -H "X-Session-ID: <sessionId>" \
  -d "look"

# View current game state
curl http://localhost:8080/game/state -H "X-Session-ID: <sessionId>"
```

## Terminal Commands

| Command | Description |
|---|---|
| `look` | Redisplay current location, items, and exits |
| `exits` | List all exits with destination names |
| `go <direction>` | Move north/south/east/west (1 move) |
| `jump` / `cross` | Special movement actions (2 moves) |
| `inventory` | List carried items |
| `take <item>` | Pick up an item |
| `drop <item>` | Drop an item |
| `examine <item>` | Inspect an item |
| `open <container>` | See what's inside a container |
| `put <item> in <container>` | Place an item into a container |
| `use <item>` | Interact with fixed objects (e.g. `use treadmill`) or eat food |
| `status` | Show score, move count, and location |
| `quests` | Show quest completion status |
| `help` | Opens the in-game manual (requires Help item) |
| `clear` | Wipe the terminal log (progress unaffected) |
| `quit` | End run, save score to leaderboard, show final stats |

## API Endpoints

### Game

- `POST /game/start` — start a session, body `{ "callsign": "..." }`, returns `sessionId`
- `POST /game/command` — submit a text command, requires `X-Session-ID` header
- `GET /game/state` — retrieve current game state, requires `X-Session-ID` header

### Leaderboard

- `POST /leaderboard/save` — upsert a score; keeps only the best run per callsign (profanity-filtered, bounds-checked)
- `GET /leaderboard/top` — top 10 global scores
- `GET /leaderboard/top-dpu` — top 10 scores from callsigns ending in `_dpu` or `.dpu`

## Build

Create a runnable JAR:

```bash
cd myproject
mvn -DskipTests package
```

Run the packaged app:

```bash
java -jar target/myproject-1.0-SNAPSHOT.jar
```

Build the frontend into the backend's static resources (so the Spring Boot JAR serves both):

```bash
cd frontend
npm run build
```

### Docker

```bash
docker build -t zork-v2 .
docker run -p 8080:8080 zork-v2
```

The image is a multi-stage build: Maven compiles the backend JAR, then a slim `eclipse-temurin` JRE image runs it.

## Project Structure

- [myproject/src/main/java/com/mygroup/App.java](myproject/src/main/java/com/mygroup/App.java) — application entry point
- [myproject/src/main/java/com/mygroup/GameController.java](myproject/src/main/java/com/mygroup/GameController.java) — game REST controller
- [myproject/src/main/java/com/mygroup/LeaderboardController.java](myproject/src/main/java/com/mygroup/LeaderboardController.java) — leaderboard REST controller
- [myproject/src/main/java/com/mygroup/GameEngine.java](myproject/src/main/java/com/mygroup/GameEngine.java) — core game logic
- [myproject/src/main/java/com/mygroup/CommandRouter.java](myproject/src/main/java/com/mygroup/CommandRouter.java) — command parsing and routing
- [myproject/src/main/java/com/mygroup/WorldBuilder.java](myproject/src/main/java/com/mygroup/WorldBuilder.java) — builds locations, items, and connections
- [myproject/src/main/java/com/mygroup/GameState.java](myproject/src/main/java/com/mygroup/GameState.java) — state model
- [myproject/src/main/java/com/mygroup/TypingChallengeSystem.java](myproject/src/main/java/com/mygroup/TypingChallengeSystem.java) — typing minigame and stadium bonus flow
- [myproject/src/main/java/com/mygroup/QuestSystem.java](myproject/src/main/java/com/mygroup/QuestSystem.java) — quest lifecycle, ranking tiers, end-game summary
- [frontend/src/App.jsx](frontend/src/App.jsx) — frontend root, terminal UI, theming, leaderboard modals
- [frontend/src/components/Sidebar.jsx](frontend/src/components/Sidebar.jsx) — operator profile, campus intel, compass
- [frontend/src/components/RightPanel.jsx](frontend/src/components/RightPanel.jsx) — cargo manifest, quest tracker, efficiency panel

## Testing

Run backend tests with:

```bash
cd myproject
mvn test
```
