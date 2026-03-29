package com.avocado.api.service;

import java.time.LocalTime;

public class SystemTimeService implements TimeService {
    @Override
    public LocalTime getCurrentTime() {
        return LocalTime.now();
    }
}
