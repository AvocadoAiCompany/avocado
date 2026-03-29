package com.avocado.api.resources;

import com.avocado.api.api.UsernameSuggestionResponse;
import com.avocado.api.services.UsernameSuggestionService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/username")
@Produces(MediaType.APPLICATION_JSON)
public class UsernameResource {

    private final UsernameSuggestionService service;

    public UsernameResource(UsernameSuggestionService service) {
        this.service = service;
    }

    @GET
    @Path("/suggest")
    public UsernameSuggestionResponse suggest(
            @NotBlank(message = "name must not be blank")
            @Size(min = 1, max = 50, message = "name must be between 1 and 50 characters")
            @QueryParam("name") String name) {
        return service.suggest(name);
    }
}
