package com.joji.demo.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);


    boolean existsByUsername(String username);
    boolean existsByUsernameAndIdNot(String username, Long id);

}
