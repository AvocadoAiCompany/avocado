package com.avocado.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RandomNumberResponse(
        @JsonProperty("result")
        long result
) {}
