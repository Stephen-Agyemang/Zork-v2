# Zork v2

Zork v2 is a Java text adventure game built with Spring Boot. You explore a campus-themed world, complete quests, manage resources, and interact with the game through a simple HTTP API.

## Features

- Multiple connected locations to explore
- Quest-based progression
- Inventory and item interactions
- Move, score, and resource tracking
- REST API for game commands and state

## Tech Stack

- Java 21
- Spring Boot 3
- Maven

## Getting Started

1. Move into the project directory:

```bash
cd myproject
```

2. Start the application:

```bash
mvn spring-boot:run
```

3. Initialize a game session:

```bash
curl -X POST http://localhost:8080/game/start
```

4. Send a command:

```bash
curl -X POST http://localhost:8080/game/command \
  -H "Content-Type: text/plain" \
  -d "look"
```

5. View current game state:

```bash
curl http://localhost:8080/game/state
```

## API Endpoints

- `POST /game/start` - Start or reset a game session
- `POST /game/command` - Submit a text command
- `GET /game/state` - Retrieve current game state
- `POST /game/debug` - Toggle debug mode

## Build

Create a runnable JAR:

```bash
mvn -DskipTests package
```

Run the packaged app:

```bash
java -jar target/myproject-1.0-SNAPSHOT.jar
```

## Project Structure

- [myproject/src/main/java/com/mygroup/App.java](myproject/src/main/java/com/mygroup/App.java) - Application entry point
- [myproject/src/main/java/com/mygroup/GameController.java](myproject/src/main/java/com/mygroup/GameController.java) - REST controller
- [myproject/src/main/java/com/mygroup/GameEngine.java](myproject/src/main/java/com/mygroup/GameEngine.java) - Core game logic
- [myproject/src/main/java/com/mygroup/GameState.java](myproject/src/main/java/com/mygroup/GameState.java) - State model

## Testing

Run tests with:

```bash
mvn test
```