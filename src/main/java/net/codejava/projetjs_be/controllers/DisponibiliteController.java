package net.codejava.projetjs_be.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.codejava.projetjs_be.dto.*;
import net.codejava.projetjs_be.entities.Utilisateur;
import net.codejava.projetjs_be.services.DisponibiliteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/disponibilites")
@RequiredArgsConstructor
public class DisponibiliteController {

    private final DisponibiliteService disponibiliteService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DisponibiliteDTO>>> getMesDisponibilites(
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(
                disponibiliteService.getMesDisponibilites(getUserId(auth))));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DisponibiliteDTO>> creer(
            @Valid @RequestBody DisponibiliteDTO dto,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(
                disponibiliteService.creer(getUserId(auth), dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> supprimer(
            @PathVariable Long id,
            Authentication auth) {
        disponibiliteService.supprimer(getUserId(auth), id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    private Long getUserId(Authentication auth) {
        return ((Utilisateur) auth.getPrincipal()).getId();
    }
}