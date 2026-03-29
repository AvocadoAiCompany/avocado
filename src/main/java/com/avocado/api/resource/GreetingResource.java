package com.avocado.api.resource;

import com.avocado.api.service.TimeService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.time.LocalTime;

@Path("/greet")
public class GreetingResource {
    private final TimeService timeService;

    public GreetingResource(TimeService timeService) {
        this.timeService = timeService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Greeting getGreeting() {
        LocalTime now = timeService.getCurrentTime();
        int hour = now.getHour();
        String message;

        if (hour >= 5 && hour < 12) {
            message = "Good morning";
        } else if (hour >= 12 && hour < 17) {
            message = "Good afternoon";
        } else if (hour >= 17 && hour < 20) {
            message = "Good evening";
        } else {
            message = "Good night";
        }

        return new Greeting(message);
    }

    public record Greeting(String message) {}
}
