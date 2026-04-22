package com.cguzman.tienda.product_service.controller;

import com.cguzman.tienda.product_service.config.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Autenticación")
public class AuthController {

    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Retorna JWT token")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        // Validación mock — en producción consultaría una BD de usuarios
        if ("admin".equals(req.getUsername()) && "admin".equals(req.getPassword())) {
            String token = jwtUtil.generateToken(req.getUsername());
            return ResponseEntity.ok(Map.of("token", token));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Credenciales incorrectas"));
    }

    @Data
    static class LoginRequest {
        private String username;
        private String password;
    }
}