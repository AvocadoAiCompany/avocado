package com.avocado.api.service;

import java.time.LocalTime;
import java.time.ZoneId;

public interface TimeService {
    LocalTime getCurrentTime();
    LocalTime getTimeForZone(ZoneId zone);
}
