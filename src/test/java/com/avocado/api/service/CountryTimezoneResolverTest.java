package com.avocado.api.service;

import jakarta.ws.rs.WebApplicationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

class CountryTimezoneResolverTest {

    private final CountryTimezoneResolver resolver = new CountryTimezoneResolver();

    @ParameterizedTest
    @CsvSource({
        "Japan,             Asia/Tokyo",
        "japan,             Asia/Tokyo",
        "JAPAN,             Asia/Tokyo",
        "United Kingdom,    Europe/London",
        "UK,                Europe/London",
        "United States,     America/New_York",
        "USA,               America/New_York",
        "France,            Europe/Paris",
        "Germany,           Europe/Berlin",
        "India,             Asia/Kolkata",
        "China,             Asia/Shanghai",
        "Australia,         Australia/Sydney",
        "Brazil,            America/Sao_Paulo",
        "UAE,               Asia/Dubai",
        "United Arab Emirates, Asia/Dubai"
    })
    void resolvesKnownCountry(String countryName, String expectedZone) {
        ZoneId zone = resolver.resolve(countryName);
        assertEquals(ZoneId.of(expectedZone), zone);
    }

    @Test
    void isCaseInsensitive() {
        assertEquals(resolver.resolve("Japan"), resolver.resolve("JAPAN"));
    }

    @Test
    void tripsLeadingAndTrailingWhitespace() {
        assertEquals(ZoneId.of("Asia/Tokyo"), resolver.resolve("  Japan  "));
    }

    @Test
    void throwsBadRequest_forUnknownCountry() {
        WebApplicationException ex = assertThrows(
            WebApplicationException.class,
            () -> resolver.resolve("Atlantis")
        );
        assertEquals(400, ex.getResponse().getStatus());
        assertTrue(ex.getMessage().contains("Atlantis"));
    }

    @Test
    void throwsBadRequest_forEmptyString() {
        assertThrows(WebApplicationException.class, () -> resolver.resolve(""));
    }
}
