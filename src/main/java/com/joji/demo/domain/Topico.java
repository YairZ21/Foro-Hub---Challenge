package com.joji.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@Entity
@Table(
        name = "topicos",
        uniqueConstraints = @UniqueConstraint(columnNames = {"titulo","mensaje"})
)
public class Topico {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(nullable = false, length = 600)
    private String mensaje;

    @Column(nullable = false, length = 100)
    private String autor;

    @Column(nullable = false, length = 100)
    private String curso;

    @Column(name = "creado_en", nullable = false)
    private LocalDateTime creadoEn; // lo ponemos en @PrePersist

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoTopico estado;     // lo ponemos en @PrePersist

    @PrePersist
    public void prePersist() {
        if (creadoEn == null) creadoEn = LocalDateTime.now();
        if (estado == null)   estado = EstadoTopico.ABIERTO;
    }
}



