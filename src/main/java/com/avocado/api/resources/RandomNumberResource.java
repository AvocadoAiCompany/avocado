package com.avocado.api.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.Random;

@Path("/random-number")
@Produces(MediaType.APPLICATION_JSON)
public class RandomNumberResource {

    @GET
    public RandomNumberResponse getRandomNumber() {
        double number = new Random().nextDouble();
        return new RandomNumberResponse(number);
    }
}
