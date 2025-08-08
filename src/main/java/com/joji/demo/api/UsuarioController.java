package com.joji.demo.api;

import com.joji.demo.api.dto.*;
import com.joji.demo.auth.Usuario;
import com.joji.demo.auth.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Map;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;

    public UsuarioController(UsuarioRepository repo, PasswordEncoder encoder) {
        this.repo = repo; this.encoder = encoder;
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody UsuarioCreateRequest dto) {
        if (repo.existsByUsername(dto.username()))
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error","username ya existe"));

        var u = new Usuario();
        u.setUsername(dto.username().trim());
        u.setPassword(encoder.encode(dto.password()));
        u.setRole(dto.role()==null ? "USER" : dto.role());

        var saved = repo.save(u);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new UsuarioResponse(saved.getId(), saved.getUsername(), saved.getRole()));
    }


    @GetMapping
    public Page<UsuarioResponse> listar(@PageableDefault(size=10) Pageable pageable) {
        return repo.findAll(pageable)
                .map(u -> new UsuarioResponse(u.getId(), u.getUsername(), u.getRole()));
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> detalle(@PathVariable Long id) {
        return repo.findById(id)
                .<ResponseEntity<?>>map(u -> ResponseEntity.ok(
                        new UsuarioResponse(u.getId(), u.getUsername(), u.getRole())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error","Usuario no encontrado","id",id)));
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @Valid @RequestBody UsuarioUpdateRequest dto) {
        var opt = repo.findById(id);
        if (opt.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error","Usuario no encontrado","id",id));

        if (repo.existsByUsernameAndIdNot(dto.username(), id))
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error","username ya existe"));

        var u = opt.get();
        u.setUsername(dto.username().trim());
        if (dto.password()!=null && !dto.password().isBlank()) {
            u.setPassword(encoder.encode(dto.password()));
        }
        if (dto.role()!=null) u.setRole(dto.role());
        var saved = repo.save(u);

        return ResponseEntity.ok(new UsuarioResponse(saved.getId(), saved.getUsername(), saved.getRole()));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> borrar(@PathVariable Long id) {
        if (!repo.existsById(id))
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error","Usuario no encontrado","id",id));
        try {
            repo.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error","No se puede eliminar: el usuario tiene respuestas/tópicos asociados"));
        }
    }
}
