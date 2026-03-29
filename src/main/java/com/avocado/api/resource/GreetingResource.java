package com.avocado.api.resource;

import com.avocado.api.service.CountryTimezoneResolver;
import com.avocado.api.service.TimeService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.time.LocalTime;
import java.time.ZoneId;

@Path("/greet")
public class GreetingResource {

    private final TimeService timeService;
    private final CountryTimezoneResolver countryTimezoneResolver;

    public GreetingResource(TimeService timeService, CountryTimezoneResolver countryTimezoneResolver) {
        this.timeService = timeService;
        this.countryTimezoneResolver = countryTimezoneResolver;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Greeting getGreeting(@QueryParam("country") String country) {
        LocalTime now;
        if (country != null && !country.isBlank()) {
            ZoneId zone = countryTimezoneResolver.resolve(country);
            now = timeService.getTimeForZone(zone);
        } else {
            now = timeService.getCurrentTime();
        }

        int hour = now.getHour();
        String message = switch (hour) {
            case int h when h >= 5 && h < 12  -> "Good morning";
            case int h when h >= 12 && h < 17 -> "Good afternoon";
            case int h when h >= 17 && h < 20 -> "Good evening";
            default                            -> "Good night";
        };

        return new Greeting(message);
    }

    public record Greeting(String message) {}
}
