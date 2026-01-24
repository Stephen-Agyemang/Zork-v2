# Zork v2 (Spring Boot backend)

Text adventure on a college campus: 13 interconnected locations, multi-part quests (DNA courier, music performance, wildlife rescue, MacBook return, treadmill sprint), and resource juggling (moves, hunger, food decay, dining coupons, typing challenges). The game engine runs as a small Spring Boot HTTP API you can drive with curl, Postman, or a web UI.

## Requirements
- Java 21+
- Maven 3.9+
- curl or any REST client

## Quickstart
1) Start the server
```bash
cd myproject
mvn spring-boot:run
```

2) Initialize the world (once per session)
```bash
curl -X POST http://localhost:8080/game/start
```

3) Send commands as plain text
```bash
curl -X POST http://localhost:8080/game/command \
	-H "Content-Type: text/plain" \
	-d "look"
```

4) Inspect current state (moves, points, location, inventory, quests)
```bash
curl http://localhost:8080/game/state
```

Optional: toggle engine debug logging while testing
```bash
curl -X POST http://localhost:8080/game/debug -H "Content-Type: application/json" -d "true"
```

## Command reference (send via `/game/command`)
- Navigation: `look`, `go <direction>`, `jump`, `cross`
- Inventory: `take <item>`, `take <item> from <container>`, `drop <item>`, `put <item> in <container>`, `inventory`
- Interaction: `examine <item>`, `use treadmill`, `status` (or `score`), `help`, `quit`

## Game systems
- Moves and scoring: actions cost moves (go=1, jump/cross=2); quests award points; optimize points per move.
- Food and hunger: carry up to 3 foods; every 5 moves hunger consumes one; empty stomach adds a 2-move penalty; foods spoil every 5 moves.
- Dining and coupons: start with 3 coupons; first dining visit locks your food source; complete both biology rescues to gain unlimited coupons.
- Quests: parallel objectives across music, DNA delivery countdown, wildlife rescue, MacBook return, treadmill sprint; multiple paths to victory.
- Typing challenge: treadmill triggers a timed word sprint; repeated bad commands can also start a penalty challenge.

## HTTP API surface
- POST `/game/start` — build the world and reset state
- POST `/game/command` — body: plain text command; returns narrative response
- GET `/game/state` — returns current `GameState` JSON (moves, points, location, inventory, quest flags, debugging and inspection)
- POST `/game/debug` — body: boolean; enable/disable debug logging

## Build and run
- Dev server: `mvn spring-boot:run`
- Package fat jar: `mvn -DskipTests package` then `java -jar target/myproject-1.0-SNAPSHOT.jar`
- Fallback direct compile (bypasses Maven quirks): `./build.sh` then `java -cp target/classes com.mygroup.App`

## Project layout
- Spring Boot entrypoint: [myproject/src/main/java/com/mygroup/App.java](myproject/src/main/java/com/mygroup/App.java)
- HTTP controller: [myproject/src/main/java/com/mygroup/GameController.java](myproject/src/main/java/com/mygroup/GameController.java)
- Engine and systems: [myproject/src/main/java/com/mygroup/GameEngine.java](myproject/src/main/java/com/mygroup/GameEngine.java), [myproject/src/main/java/com/mygroup/GameState.java](myproject/src/main/java/com/mygroup/GameState.java) and other classes under `com.mygroup`
- Tests placeholder: [myproject/src/test/java/com/mygroup/AppTest.java](myproject/src/test/java/com/mygroup/AppTest.java)

## Contributing and testing
- Run checks: `mvn test` (currently a stub; add focused tests around `GameEngine` and quest flows)
- Keep files ASCII-only unless a file already uses Unicode.

## Credits
Improved from the original freshman Software Development project, inspired by classic Zork with emphasis on quest design, resource management, and time-pressure mechanics.
