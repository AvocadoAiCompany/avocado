package com.avocado.api.resources;

import com.avocado.api.models.UsernameSuggestionResponse;
import com.avocado.api.services.UsernameSuggestionService;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(DropwizardExtensionsSupport.class)
class UsernameResourceTest {

    private static final UsernameSuggestionService service = mock(UsernameSuggestionService.class);

    private static final ResourceExtension resources = ResourceExtension.builder()
        .addResource(new UsernameResource(service))
        .build();

    @Test
    void get_returnsSuggestionsForName() {
        when(service.suggest("alice")).thenReturn(
            new UsernameSuggestionResponse("alice", List.of("alice_wafflemaster", "alice_pizzalord"))
        );

        UsernameSuggestionResponse response = resources.client()
            .target("/username-suggestions")
            .queryParam("name", "alice")
            .request()
            .get(UsernameSuggestionResponse.class);

        assertThat(response.name()).isEqualTo("alice");
        assertThat(response.suggestions()).containsExactly("alice_wafflemaster", "alice_pizzalord");
        verify(service).suggest("alice");
    }

    @Test
    void get_returns400WhenNameIsMissing() {
        Response response = resources.client()
            .target("/username-suggestions")
            .request()
            .get();

        assertThat(response.getStatus()).isEqualTo(400);
    }
}
