package com.joji.demo.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface TopicoRepository extends JpaRepository<Topico, Long> {

    boolean existsByTituloAndMensaje(String titulo, String mensaje);

    @Query("""
        SELECT t FROM Topico t
        WHERE (:curso IS NULL OR LOWER(t.curso) LIKE LOWER(CONCAT('%', :curso, '%')))
          AND (:anio  IS NULL OR YEAR(t.creadoEn) = :anio)
    """)
    Page<Topico> buscar(@Param("curso") String curso,
                        @Param("anio") Integer anio,
                        Pageable pageable);

    boolean existsByTituloAndMensajeAndIdNot(String titulo, String mensaje, Long id);

}
