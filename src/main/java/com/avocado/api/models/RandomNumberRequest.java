package com.avocado.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RandomNumberRequest(
        @NotNull
        @Size(min = 240, max = 240, message = "input must be exactly 240 characters")
        @JsonProperty("input")
        String input
) {}
