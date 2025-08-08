package com.joji.demo.api;

import com.joji.demo.api.dto.TopicoCreateRequest;
import com.joji.demo.api.dto.TopicoResponse;
import com.joji.demo.api.dto.TopicoUpdateRequest;
import com.joji.demo.domain.Topico;
import com.joji.demo.domain.TopicoRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping({"/tópicos", "/topicos"})
public class TopicoController {

    private final TopicoRepository repo;
    public TopicoController(TopicoRepository repo) { this.repo = repo; }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody TopicoCreateRequest dto,
                                   UriComponentsBuilder uri) {
        String titulo = dto.titulo().trim();
        String mensaje = dto.mensaje().trim();

        if (repo.existsByTituloAndMensaje(titulo, mensaje)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Tópico duplicado (título + mensaje)"));
        }

        Topico t = new Topico();
        t.setTitulo(titulo);
        t.setMensaje(mensaje);
        t.setAutor(dto.autor().trim());
        t.setCurso(dto.curso().trim());

        Topico saved = repo.save(t);
        URI location = uri.path("/topicos/{id}").buildAndExpand(saved.getId()).toUri();

        return ResponseEntity.created(location).body(new TopicoResponse(
                saved.getId(), saved.getTitulo(), saved.getMensaje(),
                saved.getCreadoEn(), saved.getEstado().name(),
                saved.getAutor(), saved.getCurso()
        ));
    }

    @GetMapping
    public Page<TopicoResponse> listar(
            @RequestParam(required = false) String curso,
            @RequestParam(required = false) Integer anio,
            @PageableDefault(size = 10, sort = "creadoEn", direction = Sort.Direction.ASC)
            Pageable pageable) {

        var page = (curso == null && anio == null)
                ? repo.findAll(pageable)
                : repo.buscar(curso, anio, pageable);

        return page.map(t -> new TopicoResponse(
                t.getId(), t.getTitulo(), t.getMensaje(),
                t.getCreadoEn(), t.getEstado().name(),
                t.getAutor(), t.getCurso()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detalle(@PathVariable Long id) {
        return repo.findById(id)
                .<ResponseEntity<?>>map(t -> ResponseEntity.ok(new TopicoResponse(
                        t.getId(), t.getTitulo(), t.getMensaje(),
                        t.getCreadoEn(), t.getEstado().name(),
                        t.getAutor(), t.getCurso()
                )))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Tópico no encontrado", "id", id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @Valid @RequestBody TopicoUpdateRequest dto) {
        var opt = repo.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Tópico no encontrado", "id", id));
        }

        String titulo = dto.titulo().trim();
        String mensaje = dto.mensaje().trim();
        String autor = dto.autor().trim();
        String curso = dto.curso().trim();

        if (repo.existsByTituloAndMensajeAndIdNot(titulo, mensaje, id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Tópico duplicado (título + mensaje)"));
        }

        var t = opt.get();
        t.setTitulo(titulo);
        t.setMensaje(mensaje);
        t.setAutor(autor);
        t.setCurso(curso);
        var saved = repo.save(t);

        return ResponseEntity.ok(new TopicoResponse(
                saved.getId(), saved.getTitulo(), saved.getMensaje(),
                saved.getCreadoEn(), saved.getEstado().name(),
                saved.getAutor(), saved.getCurso()
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Tópico no encontrado", "id", id));
        }
        try {
            repo.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "No se puede eliminar: está referenciado por otros registros"));
        }
    }


}
