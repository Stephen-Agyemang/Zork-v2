package com.mygroup;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;

public class RandomWordService {
    private static final String API_URL = "https://random-word-api.herokuapp.com/word?number=1";

    private final HttpClient client;
    private final Random random;

    public RandomWordService() {
        this.client = HttpClient.newHttpClient();
        this.random = new Random();
    }

    // Fetch a random word from the external API
    public String fetchRandomWord() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                return getFallbackWord();
            }

            // API returns a JSON array eg ["word"]
            String body = response.body();

            return body.replace("[", "")
                        .replace("]", "")
                        .replace("\"", "")
                        .trim();

        } catch (java.io.IOException | InterruptedException e) {
            return getFallbackWord();
        }
    }

    // Fallback method to return a random word if API call fails
    private String getFallbackWord() {
        String[] fallbackWords = {"apple", "banana", "orange", "grape", "melon", "peach", "pear", "plum", "kiwi", "mango"
        };
        return fallbackWords[random.nextInt(fallbackWords.length)];
    }
}