package com.avocado.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RandomNumberRequest(
        @NotNull
        @Size(min = 512, max = 512, message = "input must be exactly 512 characters")
        @JsonProperty("input")
        String input
) {}
