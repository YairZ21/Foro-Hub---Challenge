package com.joji.demo.api;

import com.joji.demo.api.dto.*;
import com.joji.demo.auth.UsuarioRepository;
import com.joji.demo.domain.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/respuestas")
public class RespuestaController {

    private final RespuestaRepository respuestas;
    private final TopicoRepository topicos;
    private final UsuarioRepository usuarios;

    public RespuestaController(RespuestaRepository respuestas, TopicoRepository topicos, UsuarioRepository usuarios) {
        this.respuestas = respuestas;
        this.topicos = topicos;
        this.usuarios = usuarios;
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody RespuestaCreateRequest dto,
                                   @AuthenticationPrincipal UserDetails auth,
                                   UriComponentsBuilder uri) {
        var topico = topicos.findById(dto.topicoId())
                .orElse(null);
        if (topico == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error","Tópico no encontrado","topicoId", dto.topicoId()));

        var usuario = usuarios.findByUsername(auth.getUsername())
                .orElse(null);

        var r = new Respuesta();
        r.setMensaje(dto.mensaje().trim());
        r.setTopico(topico);
        r.setUsuario(usuario);

        var saved = respuestas.save(r);
        URI location = uri.path("/respuestas/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location)
                .body(new RespuestaResponse(saved.getId(), saved.getMensaje(),
                        saved.getCreadoEn(), saved.getTopico().getId(), saved.getUsuario().getId()));
    }

    @GetMapping
    public Page<RespuestaResponse> listar(@RequestParam(required = false) Long topicoId,
                                          @RequestParam(required = false) Long usuarioId,
                                          @PageableDefault(size = 10) Pageable pageable) {
        var page = (topicoId != null) ? respuestas.findByTopicoId(topicoId, pageable)
                : (usuarioId != null) ? respuestas.findByUsuarioId(usuarioId, pageable)
                : respuestas.findAll(pageable);

        return page.map(r -> new RespuestaResponse(r.getId(), r.getMensaje(),
                r.getCreadoEn(), r.getTopico().getId(), r.getUsuario().getId()));
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> detalle(@PathVariable Long id) {
        return respuestas.findById(id)
                .<ResponseEntity<?>>map(r -> ResponseEntity.ok(new RespuestaResponse(
                        r.getId(), r.getMensaje(), r.getCreadoEn(),
                        r.getTopico().getId(), r.getUsuario().getId()
                )))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error","Respuesta no encontrada","id",id)));
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @Valid @RequestBody RespuestaUpdateRequest dto) {
        var opt = respuestas.findById(id);
        if (opt.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error","Respuesta no encontrada","id",id));

        var r = opt.get();
        r.setMensaje(dto.mensaje().trim());
        var saved = respuestas.save(r);

        return ResponseEntity.ok(new RespuestaResponse(saved.getId(), saved.getMensaje(),
                saved.getCreadoEn(), saved.getTopico().getId(), saved.getUsuario().getId()));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> borrar(@PathVariable Long id) {
        if (!respuestas.existsById(id))
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error","Respuesta no encontrada","id",id));

        respuestas.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
