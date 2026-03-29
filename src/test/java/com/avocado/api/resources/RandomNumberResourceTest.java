package com.avocado.api.resources;

import com.avocado.api.models.RandomNumberRequest;
import com.avocado.api.models.RandomNumberResponse;
import com.avocado.api.services.RandomNumberService;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(DropwizardExtensionsSupport.class)
class RandomNumberResourceTest {

    private static final ResourceExtension RESOURCES = ResourceExtension.builder()
            .addResource(new RandomNumberResource(new RandomNumberService()))
            .build();

    @Test
    void returns200WithValidInput() {
        RandomNumberRequest request = new RandomNumberRequest("a".repeat(240));
        Response response = RESOURCES.target("/random-number")
                .request()
                .post(Entity.entity(request, MediaType.APPLICATION_JSON));

        assertThat(response.getStatus()).isEqualTo(200);
        RandomNumberResponse body = response.readEntity(RandomNumberResponse.class);
        assertThat(body).isNotNull();
    }

    @Test
    void returnsDeterministicResult() {
        RandomNumberRequest request = new RandomNumberRequest("z".repeat(240));

        long first = RESOURCES.target("/random-number")
                .request()
                .post(Entity.entity(request, MediaType.APPLICATION_JSON), RandomNumberResponse.class)
                .result();

        long second = RESOURCES.target("/random-number")
                .request()
                .post(Entity.entity(request, MediaType.APPLICATION_JSON), RandomNumberResponse.class)
                .result();

        assertThat(first).isEqualTo(second);
    }

    @Test
    void returns422WhenInputTooShort() {
        RandomNumberRequest request = new RandomNumberRequest("short");
        Response response = RESOURCES.target("/random-number")
                .request()
                .post(Entity.entity(request, MediaType.APPLICATION_JSON));

        assertThat(response.getStatus()).isEqualTo(422);
    }

    @Test
    void returns422WhenInputTooLong() {
        RandomNumberRequest request = new RandomNumberRequest("a".repeat(241));
        Response response = RESOURCES.target("/random-number")
                .request()
                .post(Entity.entity(request, MediaType.APPLICATION_JSON));

        assertThat(response.getStatus()).isEqualTo(422);
    }

    @Test
    void returns422WhenInputIsNull() {
        RandomNumberRequest request = new RandomNumberRequest(null);
        Response response = RESOURCES.target("/random-number")
                .request()
                .post(Entity.entity(request, MediaType.APPLICATION_JSON));

        assertThat(response.getStatus()).isEqualTo(422);
    }
}
