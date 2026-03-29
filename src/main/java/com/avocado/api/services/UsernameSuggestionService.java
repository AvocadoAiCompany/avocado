package com.avocado.api.services;

import com.avocado.api.models.UsernameSuggestionResponse;

import java.util.List;

public class UsernameSuggestionService {

    private static final List<String> FUNNY_SUFFIXES = List.of(
        "the_destroyer",
        "wafflemaster",
        "turbochicken",
        "noodlearms",
        "thunderpants",
        "pizzalord",
        "snugglebuns",
        "of_doom",
        "mcawesome",
        "bananasplit",
        "yolo420",
        "captain_flop",
        "derpface",
        "the_magnificent",
        "nuggetking"
    );

    public UsernameSuggestionResponse suggest(String name) {
        String normalized = name.trim().toLowerCase().replaceAll("\\s+", "_");
        List<String> suggestions = FUNNY_SUFFIXES.stream()
            .map(suffix -> normalized + "_" + suffix)
            .toList();
        return new UsernameSuggestionResponse(name, suggestions);
    }
}
