package com.avocado.api.resources;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RandomNumberResourceTest {

    @Test
    void testGetRandomNumber() {
        RandomNumberResource resource = new RandomNumberResource();
        RandomNumberResponse response = resource.getRandomNumber();
        
        assertNotNull(response);
        assertTrue(response.value() >= 0.0 && response.value() <= 1.0);
    }
}
