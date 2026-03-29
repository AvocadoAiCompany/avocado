package com.avocado.api.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Random;

@Path("/random")
@Produces(MediaType.APPLICATION_JSON)
public class RandomNumberResource {

    private final Random random = new Random();

    @GET
    public RandomNumberResponse getRandomNumber() {
        int number = random.nextInt(101); // 0-100 inclusive
        return new RandomNumberResponse(number);
    }

    public record RandomNumberResponse(int number) {}
}
