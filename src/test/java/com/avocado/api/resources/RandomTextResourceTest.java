package com.avocado.api.resources;

import com.avocado.api.models.RandomTextRequest;
import com.avocado.api.models.RandomTextResponse;
import com.avocado.api.services.RandomTextService;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(DropwizardExtensionsSupport.class)
class RandomTextResourceTest {

    private static final RandomTextService mockService = mock(RandomTextService.class);

    private static final ResourceExtension RESOURCES = ResourceExtension.builder()
            .addResource(new RandomTextResource(mockService))
            .build();

    @Test
    void post_validRequest_returns200WithResponse() {
        var request = new RandomTextRequest("hello world");
        var expectedResponse = new RandomTextResponse("hello world", "Nebula orbit prism.", "hello world".hashCode());
        when(mockService.generate(request)).thenReturn(expectedResponse);

        Response response = RESOURCES.target("/random-text")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(request));

        assertThat(response.getStatus()).isEqualTo(200);
        var body = response.readEntity(RandomTextResponse.class);
        assertThat(body.inputText()).isEqualTo("hello world");
        assertThat(body.randomText()).isEqualTo("Nebula orbit prism.");
    }

    @Test
    void post_blankInputText_returns422() {
        var request = new RandomTextRequest("   ");

        Response response = RESOURCES.target("/random-text")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(request));

        assertThat(response.getStatus()).isEqualTo(422);
    }

    @Test
    void post_inputTextExceeds240Characters_returns422() {
        var request = new RandomTextRequest("a".repeat(241));

        Response response = RESOURCES.target("/random-text")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(request));

        assertThat(response.getStatus()).isEqualTo(422);
    }

    @Test
    void post_nullBody_returns422() {
        Response response = RESOURCES.target("/random-text")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(null));

        assertThat(response.getStatus()).isEqualTo(422);
    }

    @Test
    void post_inputTextExactly240Characters_returns200() {
        String maxInput = "b".repeat(240);
        var request = new RandomTextRequest(maxInput);
        var expectedResponse = new RandomTextResponse(maxInput, "Some random text.", maxInput.hashCode());
        when(mockService.generate(request)).thenReturn(expectedResponse);

        Response response = RESOURCES.target("/random-text")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(request));

        assertThat(response.getStatus()).isEqualTo(200);
    }
}
