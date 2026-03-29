package com.avocado.api.resources;

import com.avocado.api.model.RandomNumber;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/random")
@Produces(MediaType.APPLICATION_JSON)
public class RandomNumberResource {

    @GET
    public Response getRandomNumber() {
        double number = Math.random();
        return Response.ok(new RandomNumber(number)).build();
    }
}
