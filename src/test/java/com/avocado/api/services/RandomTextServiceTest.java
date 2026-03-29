package com.avocado.api.services;

import com.avocado.api.models.RandomTextRequest;
import com.avocado.api.models.RandomTextResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RandomTextServiceTest {

    private RandomTextService service;

    @BeforeEach
    void setUp() {
        service = new RandomTextService();
    }

    @Test
    void generate_returnsResponseWithMatchingInputText() {
        var request = new RandomTextRequest("hello world");
        RandomTextResponse response = service.generate(request);
        assertThat(response.inputText()).isEqualTo("hello world");
    }

    @Test
    void generate_returnsNonBlankRandomText() {
        var request = new RandomTextRequest("some input");
        RandomTextResponse response = service.generate(request);
        assertThat(response.randomText()).isNotBlank();
    }

    @Test
    void generate_randomTextEndWithPeriod() {
        var request = new RandomTextRequest("some input");
        RandomTextResponse response = service.generate(request);
        assertThat(response.randomText()).endsWith(".");
    }

    @Test
    void generate_sameInputProducesSameOutput() {
        var request1 = new RandomTextRequest("deterministic input");
        var request2 = new RandomTextRequest("deterministic input");

        RandomTextResponse response1 = service.generate(request1);
        RandomTextResponse response2 = service.generate(request2);

        assertThat(response1.randomText()).isEqualTo(response2.randomText());
        assertThat(response1.seed()).isEqualTo(response2.seed());
    }

    @Test
    void generate_differentInputsProduceDifferentOutput() {
        var request1 = new RandomTextRequest("input alpha");
        var request2 = new RandomTextRequest("input beta");

        RandomTextResponse response1 = service.generate(request1);
        RandomTextResponse response2 = service.generate(request2);

        assertThat(response1.randomText()).isNotEqualTo(response2.randomText());
    }

    @Test
    void generate_seedMatchesInputHashCode() {
        String input = "seed check";
        var request = new RandomTextRequest(input);
        RandomTextResponse response = service.generate(request);
        assertThat(response.seed()).isEqualTo(input.hashCode());
    }

    @Test
    void generate_randomTextStartsWithCapitalLetter() {
        var request = new RandomTextRequest("capitalize test");
        RandomTextResponse response = service.generate(request);
        assertThat(response.randomText().charAt(0)).isUpperCase();
    }

    @Test
    void generate_handlesMaxLengthInput() {
        String maxInput = "a".repeat(240);
        var request = new RandomTextRequest(maxInput);
        RandomTextResponse response = service.generate(request);
        assertThat(response.randomText()).isNotBlank();
    }
}
