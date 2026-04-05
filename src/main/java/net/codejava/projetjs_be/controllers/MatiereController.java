package net.codejava.projetjs_be.controllers;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import net.codejava.projetjs_be.dto.ApiResponse;
import net.codejava.projetjs_be.dto.MatiereDTO;
import net.codejava.projetjs_be.entities.Utilisateur;
import net.codejava.projetjs_be.services.MatiereService;

@RestController
@RequestMapping("/api/matieres")
@RequiredArgsConstructor
public class MatiereController {

    private final MatiereService matiereService;

    private Long getUserId(Authentication auth) {
        // récupère l'id depuis le token
        return ((Utilisateur) auth.getPrincipal()).getId();
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MatiereDTO>>> getMesMatieres(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(
                matiereService.getMessMatieres(getUserId(auth))));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MatiereDTO>> creer(
            @Valid @RequestBody MatiereDTO dto, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(
                matiereService.creer(getUserId(auth), dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MatiereDTO>> modifier(
            @PathVariable Long id,
            @Valid @RequestBody MatiereDTO dto, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(
                matiereService.modifier(getUserId(auth), id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> supprimer(
            @PathVariable Long id, Authentication auth) {
        matiereService.supprimer(getUserId(auth), id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}