package com.avocado.api.resources;

import com.avocado.api.api.UsernameSuggestionResponse;
import com.avocado.api.services.UsernameSuggestionService;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(DropwizardExtensionsSupport.class)
class UsernameResourceTest {

    private static final UsernameSuggestionService service = mock(UsernameSuggestionService.class);

    private static final ResourceExtension resources = ResourceExtension.builder()
            .addResource(new UsernameResource(service))
            .build();

    @Test
    void suggest_returns200WithValidName() {
        when(service.suggest("alice")).thenReturn(
                new UsernameSuggestionResponse("alice", java.util.List.of("alice_wizard"))
        );

        UsernameSuggestionResponse response = resources.target("/username/suggest")
                .queryParam("name", "alice")
                .request()
                .get(UsernameSuggestionResponse.class);

        assertThat(response.originalName()).isEqualTo("alice");
        assertThat(response.suggestions()).contains("alice_wizard");
    }

    @Test
    void suggest_returns400WhenNameIsMissing() {
        Response response = resources.target("/username/suggest")
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(422);
        assertThat(response.readEntity(String.class)).contains("name must not be blank");
    }

    @Test
    void suggest_returns400WhenNameIsBlank() {
        Response response = resources.target("/username/suggest")
                .queryParam("name", "   ")
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(422);
        assertThat(response.readEntity(String.class)).contains("name must not be blank");
    }

    @Test
    void suggest_returns400WhenNameExceedsMaxLength() {
        String longName = "a".repeat(51);

        Response response = resources.target("/username/suggest")
                .queryParam("name", longName)
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(422);
        assertThat(response.readEntity(String.class)).contains("name must be between 1 and 50 characters");
    }
}
