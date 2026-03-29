package com.avocado.api.services;

import com.avocado.api.api.UsernameSuggestionResponse;

import java.util.List;
import java.util.Random;

public class UsernameSuggestionService {

    private static final List<String> FUNNY_SUFFIXES = List.of(
            "the_great", "42", "wizard", "ninja", "supreme", "turbo", "ultra", "prime"
    );

    private final Random random = new Random();

    public UsernameSuggestionResponse suggest(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        String normalized = name.trim().toLowerCase().replaceAll("\\s+", "_");

        List<String> suggestions = FUNNY_SUFFIXES.stream()
                .map(suffix -> normalized + "_" + suffix)
                .toList();

        return new UsernameSuggestionResponse(name, suggestions);
    }
}
