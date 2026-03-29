package com.avocado.api.resources;

import com.avocado.api.models.RandomTextRequest;
import com.avocado.api.models.RandomTextResponse;
import com.avocado.api.services.RandomTextService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/random-text")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RandomTextResource {

    private final RandomTextService randomTextService;

    public RandomTextResource(RandomTextService randomTextService) {
        this.randomTextService = randomTextService;
    }

    @POST
    public Response generate(@NotNull @Valid RandomTextRequest request) {
        RandomTextResponse response = randomTextService.generate(request);
        return Response.ok(response).build();
    }
}
