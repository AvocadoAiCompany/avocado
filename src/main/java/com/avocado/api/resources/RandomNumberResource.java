package com.avocado.api.resources;

import com.avocado.api.models.RandomNumberRequest;
import com.avocado.api.models.RandomNumberResponse;
import com.avocado.api.services.RandomNumberService;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/random-number")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RandomNumberResource {

    private final RandomNumberService randomNumberService;

    public RandomNumberResource(RandomNumberService randomNumberService) {
        this.randomNumberService = randomNumberService;
    }

    @POST
    public RandomNumberResponse generate(@Valid RandomNumberRequest request) {
        long result = randomNumberService.generate(request.input());
        return new RandomNumberResponse(result);
    }
}
