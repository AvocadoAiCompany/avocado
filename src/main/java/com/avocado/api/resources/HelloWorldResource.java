package com.avocado.api.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/hello")
@Produces(MediaType.APPLICATION_JSON)
public class HelloWorldResource {

    @GET
    public HelloResponse hello() {
        return new HelloResponse("Hello from Avocado API!");
    }

    public record HelloResponse(String message) {}
}
