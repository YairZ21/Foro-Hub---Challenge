package com.joji.demo.api.dto;

import jakarta.validation.constraints.*;

public record UsuarioUpdateRequest(
        @NotBlank @Size(max=100) String username,
        @Size(min=6, max=100) String password,           // opcional
        @Pattern(regexp="USER|ADMIN") String role        // opcional
) {}

