package com.avocado.api.services;

import com.avocado.api.api.UsernameSuggestionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UsernameSuggestionServiceTest {

    private UsernameSuggestionService service;

    @BeforeEach
    void setUp() {
        service = new UsernameSuggestionService();
    }

    @Test
    void suggest_returnsOriginalNameInResponse() {
        UsernameSuggestionResponse response = service.suggest("Alice");

        assertThat(response.originalName()).isEqualTo("Alice");
    }

    @Test
    void suggest_returnsSuggestions() {
        UsernameSuggestionResponse response = service.suggest("Alice");

        assertThat(response.suggestions()).isNotEmpty();
    }

    @Test
    void suggest_normalizesInputCorrectly() {
        UsernameSuggestionResponse response = service.suggest("  John Doe  ");

        assertThat(response.suggestions()).allMatch(s -> s.startsWith("john_doe_"));
    }

    @Test
    void suggest_normalizesMultipleSpaces() {
        UsernameSuggestionResponse response = service.suggest("Jane   Smith");

        assertThat(response.suggestions()).allMatch(s -> s.startsWith("jane_smith_"));
    }

    @Test
    void suggest_lowercasesInput() {
        UsernameSuggestionResponse response = service.suggest("ALLCAPS");

        assertThat(response.suggestions()).allMatch(s -> s.startsWith("allcaps_"));
    }

    @Test
    void suggest_throwsOnNullName() {
        assertThatThrownBy(() -> service.suggest(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Name cannot be empty");
    }

    @Test
    void suggest_throwsOnBlankName() {
        assertThatThrownBy(() -> service.suggest("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Name cannot be empty");
    }
}
