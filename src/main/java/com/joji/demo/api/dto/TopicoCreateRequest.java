package com.joji.demo.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TopicoCreateRequest(
        @NotBlank @Size(max=100) String titulo,
        @NotBlank @Size(max=600) String mensaje,
        @NotBlank @Size(max=100) String autor,
        @NotBlank @Size(max=100) String curso
) {}

