package com.avocado.api.models;

public record RandomTextResponse(
        String inputText,
        String randomText,
        int seed
) {}
