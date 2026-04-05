package net.codejava.projetjs_be.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.codejava.projetjs_be.dto.*;
import net.codejava.projetjs_be.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<TokenDTO>> inscrire(
            @Valid @RequestBody RegisterDTO dto) {
        TokenDTO token = authService.inscrire(dto);
        return ResponseEntity.ok(ApiResponse.ok(token));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenDTO>> connecter(
            @Valid @RequestBody LoginDTO dto) {
        TokenDTO token = authService.connecter(dto);
        return ResponseEntity.ok(ApiResponse.ok(token));
    }
}