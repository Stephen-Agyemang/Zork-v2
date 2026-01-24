package com.mygroup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * App - Single entry point for Zork v2 game
 * Consolidated from Main.java, TestMain.java, and original App.java
 */

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
        System.out.println("Welcome to Zork v2! Server is running on port 8080");
        }
}

