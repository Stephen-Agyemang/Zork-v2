package com.mygroup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/game")
public class GameController {

    @Autowired
    private final GameEngine gameEngine;

    public GameController(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    @PostMapping("/start")
    public String startGame() {
        gameEngine.createWorld();
        return "Game world created, ready to play!";
    }

    @PostMapping("/command")
    public String processCommand(@RequestBody String userInput) {
        return gameEngine.processCommand(userInput);
    }

    //Return current game state as JSON
    @GetMapping("/state")
    public GameState getGameState(){
        return gameEngine.getGameState();
    }

    //Toggle debug logging
    @PostMapping("/debug")
    public String setDebugLogging(@RequestBody boolean enabled) {
        gameEngine.setDebugLogging(enabled);
        return "Debug loogging" + (enabled ? " enabled." : " disabled.");
    }
}
