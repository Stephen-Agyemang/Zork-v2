# Zork v2

Zork v2 is a full-stack, campus-themed text adventure game. A Spring Boot REST API drives the game world and session state; a React/Vite terminal-style frontend renders the experience with selectable visual themes, a callsign/profile flow, and global + campus-specific leaderboards.

## Features

- Campus-themed world with 13 connected locations, quests, items, and containers
- Cardinal movement (`go north/south/east/west`, 1 move) plus special `jump` and `cross` moves (2 moves each)
- Inventory, container, and item interactions
- Hunger/food system, DNA delivery countdown, typing-challenge minigame
- Session-isolated concurrent games — many players can play at once on one server
- `clear` terminal command to wipe the visible log without affecting progress
- Persistent leaderboard: global rankings + a DePauw-only leaderboard (callsigns ending in `_dpu` or `.dpu`)
- Server-side rate limiting and profanity filtering on score submissions
- 5 selectable UI themes (amber, green, phantom, steampunk, archive) with location-specific background photography

## Tech Stack

- **Backend:** Java 21, Spring Boot 3, Spring Data JPA
- **Database:** PostgreSQL in production, H2 in-memory fallback for local development
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

## API Endpoints

### Game

- `POST /game/start` — start a session, body `{ "callsign": "..." }`, returns `sessionId`
- `POST /game/command` — submit a text command, requires `X-Session-ID` header
- `GET /game/state` — retrieve current game state, requires `X-Session-ID` header

### Leaderboard

- `POST /leaderboard/save` — submit a completed run's score (rate-limited per IP, profanity-filtered)
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
- [frontend/src/App.jsx](frontend/src/App.jsx) — frontend root, terminal UI, theming, leaderboard modals

## Testing

Run backend tests with:

```bash
cd myproject
mvn test
```