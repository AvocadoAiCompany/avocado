package com.avocado.api.models;

import java.util.List;

public record UsernameSuggestionResponse(String name, List<String> suggestions) {}
