package com.joji.demo.api.dto;

import java.time.LocalDateTime;

public record TopicoResponse(
        Long id,
        String titulo,
        String mensaje,
        LocalDateTime creadoEn,
        String estado,
        String autor,
        String curso
) {}

