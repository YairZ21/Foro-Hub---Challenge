package com.joji.demo.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RespuestaUpdateRequest(
        @NotBlank @Size(max=1000) String mensaje
) {}
