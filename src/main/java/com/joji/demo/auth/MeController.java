package com.joji.demo.auth;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class MeController {
    @GetMapping("/me")
    public Object me(Authentication auth) {
        return auth; // verás principal, roles, etc.
    }
}
