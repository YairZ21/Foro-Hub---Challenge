package com.joji.demo.api.dto;

import java.time.LocalDateTime;

public record RespuestaResponse(
        Long id,
        String mensaje,
        LocalDateTime creadoEn,
        Long topicoId,
        Long usuarioId
) {}
