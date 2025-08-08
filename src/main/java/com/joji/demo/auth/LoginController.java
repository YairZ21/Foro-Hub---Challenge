package com.joji.demo.auth;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoginController {

    private final AuthenticationManager authManager;
    private final TokenService tokens;

    public LoginController(AuthenticationManager authManager, TokenService tokens) {
        this.authManager = authManager;
        this.tokens = tokens;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password())
        );
        var user = (UserDetails) auth.getPrincipal();
        return ResponseEntity.ok(new JwtResponse(tokens.generarToken(user)));
    }
}
