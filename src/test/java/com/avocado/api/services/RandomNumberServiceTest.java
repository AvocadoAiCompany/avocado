package com.avocado.api.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RandomNumberServiceTest {

    private RandomNumberService service;

    @BeforeEach
    void setUp() {
        service = new RandomNumberService();
    }

    @Test
    void sameInputProducesSameResult() {
        String input = "a".repeat(512);
        assertThat(service.generate(input)).isEqualTo(service.generate(input));
    }

    @Test
    void differentInputsProduceDifferentResults() {
        String inputA = "a".repeat(512);
        String inputB = "b".repeat(512);
        assertThat(service.generate(inputA)).isNotEqualTo(service.generate(inputB));
    }

    @Test
    void inputWithMixedCharactersIsStable() {
        String input = "Hello World! ".repeat(39) + "abcde";
        long first = service.generate(input);
        long second = service.generate(input);
        assertThat(first).isEqualTo(second);
    }
}
