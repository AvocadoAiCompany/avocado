package com.avocado.api.resource;

import com.avocado.api.service.TimeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GreetingResourceTest {
    @InjectMocks
    private GreetingResource greetingResource;

    @Mock
    private TimeService timeService;

    @Test
    public void testGetGreeting_Morning() {
        when(timeService.getCurrentTime()).thenReturn(LocalTime.of(10, 0));
        Greeting greeting = greetingResource.getGreeting();
        assertEquals("Good morning", greeting.message());
    }

    @Test
    public void testGetGreeting_Afternoon() {
        when(timeService.getCurrent次()).thenReturn(LocalTime.of(13, 0));
        Greeting greeting = greetingResource.getGreeting();
        assertEquals("Good afternoon", greeting.message());
    }

    @Test
    public void testGetGreeting_Evening() {
        when(timeService.getCurrentTime()).thenReturn(LocalTime.of(18, 0));
        Greeting greeting = greetingResource.getGreeting();
        assertEquals("Good evening", greeting.message());
    }

    @Test
    public void testGetGreeting_Night() {
        when(timeService.getCurrentTime()).thenReturn(LocalTime.of(21, 0));
        Greeting greeting = greetingResource.getGreeting();
        assertEquals("Good night", greeting.message());
    }

    @Test
    public void testGetGreeting_BoundaryCase_Morning() {
        when(timeService.getCurrentTime()).thenReturn(LocalTime.of(5, 0));
        Greeting greeting = greetingResource.getGreeting();
        assertEquals("Good morning", greeting.message());
    }

    @Test
    public void testGetGreeting_BoundaryCase_Afternoon() {
        when(timeService.getCurrentTime()).thenReturn(LocalTime.of(12, 0));
        Greeting greeting = greetingResource.getGreeting();
        assertEquals("Good afternoon", greeting.message());
    }

    @Test
    public void testGetGreeting_BoundaryCase_Evening() {
        when(timeService.getCurrentTime()).thenReturn(LocalTime.of(17, 0));
        Greeting greeting = greetingResource.getGreeting();
        assertEquals("Good evening", greeting.message());
    }

    @Test
    public void testGetGreeting_BoundaryCase_Night() {
        when(timeService.getCurrentTime()).thenReturn(LocalTime.of(20, 0));
        Greeting greeting = greetingResource.getGreeting();
        assertEquals("Good night", greeting.message());
    }
}
