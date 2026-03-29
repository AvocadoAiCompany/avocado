package com.avocado.api;

import com.avocado.api.resources.HelloWorldResource;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(DropwizardExtensionsSupport.class)
class HelloWorldResourceTest {

    private static final ResourceExtension EXT = ResourceExtension.builder()
            .addResource(new HelloWorldResource())
            .build();

    @Test
    void helloReturns200() {
        try (Response response = EXT.target("/hello").request().get()) {
            assertEquals(200, response.getStatus());
        }
    }

    @Test
    void helloReturnsMessage() {
        HelloWorldResource.HelloResponse body =
                EXT.target("/hello").request().get(HelloWorldResource.HelloResponse.class);
        assertEquals("Hello from Avocado API!", body.message());
    }
}
