package com.avocado.api.resources;

import com.avocado.api.models.UsernameSuggestionResponse;
import com.avocado.api.services.UsernameSuggestionService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/username-suggestions")
@Produces(MediaType.APPLICATION_JSON)
public class UsernameResource {

    private final UsernameSuggestionService service;

    public UsernameResource(UsernameSuggestionService service) {
        this.service = service;
    }

    @GET
    public UsernameSuggestionResponse suggest(
        @QueryParam("name") @NotBlank @Size(min = 1, max = 50) String name
    ) {
        return service.suggest(name);
    }
}
