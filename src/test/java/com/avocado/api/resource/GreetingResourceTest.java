package com.avocado.api.resource;

import com.avocado.api.service.CountryTimezoneResolver;
import com.avocado.api.service.TimeService;
import jakarta.ws.rs.WebApplicationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GreetingResourceTest {

    @Mock
    private TimeService timeService;

    @Mock
    private CountryTimezoneResolver countryTimezoneResolver;

    @InjectMocks
    private GreetingResource greetingResource;

    // --- no-country (system time) ---

    @Test
    void greetsMorning_noCountry() {
        when(timeService.getCurrentTime()).thenReturn(LocalTime.of(10, 0));
        assertEquals("Good morning", greetingResource.getGreeting(null).message());
    }

    @Test
    void greetsAfternoon_noCountry() {
        when(timeService.getCurrentTime()).thenReturn(LocalTime.of(13, 0));
        assertEquals("Good afternoon", greetingResource.getGreeting(null).message());
    }

    @Test
    void greetsEvening_noCountry() {
        when(timeService.getCurrentTime()).thenReturn(LocalTime.of(18, 0));
        assertEquals("Good evening", greetingResource.getGreeting(null).message());
    }

    @Test
    void greetsNight_noCountry() {
        when(timeService.getCurrentTime()).thenReturn(LocalTime.of(21, 0));
        assertEquals("Good night", greetingResource.getGreeting(null).message());
    }

    @Test
    void greetsNight_blankCountry() {
        when(timeService.getCurrentTime()).thenReturn(LocalTime.of(2, 0));
        assertEquals("Good night", greetingResource.getGreeting("  ").message());
    }

    // --- boundary cases (system time) ---

    @Test
    void boundaryMorningStart() {
        when(timeService.getCurrentTime()).thenReturn(LocalTime.of(5, 0));
        assertEquals("Good morning", greetingResource.getGreeting(null).message());
    }

    @Test
    void boundaryAfternoonStart() {
        when(timeService.getCurrentTime()).thenReturn(LocalTime.of(12, 0));
        assertEquals("Good afternoon", greetingResource.getGreeting(null).message());
    }

    @Test
    void boundaryEveningStart() {
        when(timeService.getCurrentTime()).thenReturn(LocalTime.of(17, 0));
        assertEquals("Good evening", greetingResource.getGreeting(null).message());
    }

    @Test
    void boundaryNightStart() {
        when(timeService.getCurrentTime()).thenReturn(LocalTime.of(20, 0));
        assertEquals("Good night", greetingResource.getGreeting(null).message());
    }

    // --- with country ---

    @Test
    void greetsMorning_withCountry() {
        ZoneId tokyo = ZoneId.of("Asia/Tokyo");
        when(countryTimezoneResolver.resolve("Japan")).thenReturn(tokyo);
        when(timeService.getTimeForZone(tokyo)).thenReturn(LocalTime.of(8, 30));

        assertEquals("Good morning", greetingResource.getGreeting("Japan").message());
        verify(timeService, never()).getCurrentTime();
    }

    @Test
    void greetsAfternoon_withCountry() {
        ZoneId london = ZoneId.of("Europe/London");
        when(countryTimezoneResolver.resolve("United Kingdom")).thenReturn(london);
        when(timeService.getTimeForZone(london)).thenReturn(LocalTime.of(14, 0));

        assertEquals("Good afternoon", greetingResource.getGreeting("United Kingdom").message());
    }

    @Test
    void greetsEvening_withCountry() {
        ZoneId paris = ZoneId.of("Europe/Paris");
        when(countryTimezoneResolver.resolve("France")).thenReturn(paris);
        when(timeService.getTimeForZone(paris)).thenReturn(LocalTime.of(19, 0));

        assertEquals("Good evening", greetingResource.getGreeting("France").message());
    }

    @Test
    void greetsNight_withCountry() {
        ZoneId nyc = ZoneId.of("America/New_York");
        when(countryTimezoneResolver.resolve("United States")).thenReturn(nyc);
        when(timeService.getTimeForZone(nyc)).thenReturn(LocalTime.of(23, 0));

        assertEquals("Good night", greetingResource.getGreeting("United States").message());
    }

    @Test
    void throwsBadRequest_unknownCountry() {
        when(countryTimezoneResolver.resolve("Narnia"))
            .thenThrow(new WebApplicationException("Unknown country: Narnia", 400));

        WebApplicationException ex = assertThrows(
            WebApplicationException.class,
            () -> greetingResource.getGreeting("Narnia")
        );
        assertEquals(400, ex.getResponse().getStatus());
    }
}
