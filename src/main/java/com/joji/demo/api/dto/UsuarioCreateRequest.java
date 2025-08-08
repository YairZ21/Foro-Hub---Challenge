package com.joji.demo.api.dto;

import jakarta.validation.constraints.*;

public record UsuarioCreateRequest(
        @NotBlank @Size(max=100) String username,
        @NotBlank @Size(min=6, max=100) String password,
        @Pattern(regexp="USER|ADMIN") String role
) {}
