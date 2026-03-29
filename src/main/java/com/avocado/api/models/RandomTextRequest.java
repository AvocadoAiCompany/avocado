package com.avocado.api.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RandomTextRequest(
        @NotBlank(message = "Input text must not be blank")
        @Size(max = 240, message = "Input text must not exceed 240 characters")
        String inputText
) {}
