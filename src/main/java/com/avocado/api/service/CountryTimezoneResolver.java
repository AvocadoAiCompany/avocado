package com.avocado.api.service;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.time.ZoneId;
import java.util.Map;

public class CountryTimezoneResolver {

    private static final Map<String, ZoneId> ZONES = Map.ofEntries(
        Map.entry("united states",             ZoneId.of("America/New_York")),
        Map.entry("usa",                        ZoneId.of("America/New_York")),
        Map.entry("united kingdom",             ZoneId.of("Europe/London")),
        Map.entry("uk",                         ZoneId.of("Europe/London")),
        Map.entry("france",                     ZoneId.of("Europe/Paris")),
        Map.entry("germany",                    ZoneId.of("Europe/Berlin")),
        Map.entry("japan",                      ZoneId.of("Asia/Tokyo")),
        Map.entry("china",                      ZoneId.of("Asia/Shanghai")),
        Map.entry("india",                      ZoneId.of("Asia/Kolkata")),
        Map.entry("australia",                  ZoneId.of("Australia/Sydney")),
        Map.entry("brazil",                     ZoneId.of("America/Sao_Paulo")),
        Map.entry("canada",                     ZoneId.of("America/Toronto")),
        Map.entry("russia",                     ZoneId.of("Europe/Moscow")),
        Map.entry("south korea",                ZoneId.of("Asia/Seoul")),
        Map.entry("mexico",                     ZoneId.of("America/Mexico_City")),
        Map.entry("argentina",                  ZoneId.of("America/Argentina/Buenos_Aires")),
        Map.entry("south africa",               ZoneId.of("Africa/Johannesburg")),
        Map.entry("nigeria",                    ZoneId.of("Africa/Lagos")),
        Map.entry("egypt",                      ZoneId.of("Africa/Cairo")),
        Map.entry("kenya",                      ZoneId.of("Africa/Nairobi")),
        Map.entry("spain",                      ZoneId.of("Europe/Madrid")),
        Map.entry("italy",                      ZoneId.of("Europe/Rome")),
        Map.entry("netherlands",                ZoneId.of("Europe/Amsterdam")),
        Map.entry("sweden",                     ZoneId.of("Europe/Stockholm")),
        Map.entry("norway",                     ZoneId.of("Europe/Oslo")),
        Map.entry("denmark",                    ZoneId.of("Europe/Copenhagen")),
        Map.entry("finland",                    ZoneId.of("Europe/Helsinki")),
        Map.entry("poland",                     ZoneId.of("Europe/Warsaw")),
        Map.entry("turkey",                     ZoneId.of("Europe/Istanbul")),
        Map.entry("saudi arabia",               ZoneId.of("Asia/Riyadh")),
        Map.entry("united arab emirates",       ZoneId.of("Asia/Dubai")),
        Map.entry("uae",                        ZoneId.of("Asia/Dubai")),
        Map.entry("singapore",                  ZoneId.of("Asia/Singapore")),
        Map.entry("thailand",                   ZoneId.of("Asia/Bangkok")),
        Map.entry("vietnam",                    ZoneId.of("Asia/Ho_Chi_Minh")),
        Map.entry("indonesia",                  ZoneId.of("Asia/Jakarta")),
        Map.entry("philippines",                ZoneId.of("Asia/Manila")),
        Map.entry("malaysia",                   ZoneId.of("Asia/Kuala_Lumpur")),
        Map.entry("pakistan",                   ZoneId.of("Asia/Karachi")),
        Map.entry("bangladesh",                 ZoneId.of("Asia/Dhaka")),
        Map.entry("sri lanka",                  ZoneId.of("Asia/Colombo")),
        Map.entry("new zealand",                ZoneId.of("Pacific/Auckland")),
        Map.entry("portugal",                   ZoneId.of("Europe/Lisbon")),
        Map.entry("switzerland",                ZoneId.of("Europe/Zurich")),
        Map.entry("austria",                    ZoneId.of("Europe/Vienna")),
        Map.entry("belgium",                    ZoneId.of("Europe/Brussels")),
        Map.entry("greece",                     ZoneId.of("Europe/Athens")),
        Map.entry("israel",                     ZoneId.of("Asia/Jerusalem")),
        Map.entry("iran",                       ZoneId.of("Asia/Tehran")),
        Map.entry("iraq",                       ZoneId.of("Asia/Baghdad"))
    );

    public ZoneId resolve(String countryName) {
        ZoneId zone = ZONES.get(countryName.trim().toLowerCase());
        if (zone == null) {
            throw new WebApplicationException(
                "Unknown country: " + countryName,
                Response.Status.BAD_REQUEST
            );
        }
        return zone;
    }
}
