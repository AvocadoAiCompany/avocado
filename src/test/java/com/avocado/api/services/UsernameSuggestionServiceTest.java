package com.avocado.api.services;

import com.avocado.api.models.UsernameSuggestionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UsernameSuggestionServiceTest {

    private UsernameSuggestionService service;

    @BeforeEach
    void setUp() {
        service = new UsernameSuggestionService();
    }

    @Test
    void suggest_returnsOriginalNameInResponse() {
        UsernameSuggestionResponse response = service.suggest("Alice");
        assertThat(response.name()).isEqualTo("Alice");
    }

    @Test
    void suggest_returnsNonEmptySuggestions() {
        UsernameSuggestionResponse response = service.suggest("Alice");
        assertThat(response.suggestions()).isNotEmpty();
    }

    @Test
    void suggest_suggestionsContainNormalizedName() {
        UsernameSuggestionResponse response = service.suggest("Alice");
        assertThat(response.suggestions()).allMatch(s -> s.startsWith("alice_"));
    }

    @Test
    void suggest_normalizesSpacesToUnderscores() {
        UsernameSuggestionResponse response = service.suggest("John Doe");
        assertThat(response.suggestions()).allMatch(s -> s.startsWith("john_doe_"));
    }

    @Test
    void suggest_normalizesToLowerCase() {
        UsernameSuggestionResponse response = service.suggest("CAPS");
        assertThat(response.suggestions()).allMatch(s -> s.startsWith("caps_"));
    }

    @Test
    void suggest_trimsWhitespace() {
        UsernameSuggestionResponse response = service.suggest("  bob  ");
        assertThat(response.suggestions()).allMatch(s -> s.startsWith("bob_"));
    }
}
