package com.avocado.api.service;

import java.time.LocalTime;
import java.time.ZoneId;

public class SystemTimeService implements TimeService {
    @Override
    public LocalTime getCurrentTime() {
        return LocalTime.now();
    }

    @Override
    public LocalTime getTimeForZone(ZoneId zone) {
        return LocalTime.now(zone);
    }
}
