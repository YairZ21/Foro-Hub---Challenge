package com.joji.demo.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RespuestaCreateRequest(
        @NotBlank String mensaje,
        @NotNull  Long topicoId
) {}

