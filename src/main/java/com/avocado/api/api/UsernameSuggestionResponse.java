package com.avocado.api.api;

import java.util.List;

public record UsernameSuggestionResponse(String originalName, List<String> suggestions) {}
