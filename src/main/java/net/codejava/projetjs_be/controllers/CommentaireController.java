package net.codejava.projetjs_be.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.codejava.projetjs_be.dto.*;
import net.codejava.projetjs_be.entities.Utilisateur;
import net.codejava.projetjs_be.services.CommentaireService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions/{sessionId}/commentaires")
@RequiredArgsConstructor
public class CommentaireController {

    private final CommentaireService commentaireService;

    // GET /api/sessions/{sessionId}/commentaires
    @GetMapping
    public ResponseEntity<ApiResponse<List<CommentaireDTO>>> getCommentaires(
            @PathVariable Long sessionId) {
        return ResponseEntity.ok(ApiResponse.ok(
                commentaireService.getCommentaires(sessionId)));
    }

    // POST /api/sessions/{sessionId}/commentaires
    @PostMapping
    public ResponseEntity<ApiResponse<CommentaireDTO>> ajouter(
            @PathVariable Long sessionId,
            @Valid @RequestBody CommentaireDTO dto,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(
                commentaireService.ajouter(getUserId(auth), sessionId, dto)));
    }

    // PUT /api/sessions/{sessionId}/commentaires/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CommentaireDTO>> modifier(
            @PathVariable Long sessionId,
            @PathVariable Long id,
            @Valid @RequestBody CommentaireDTO dto,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(
                commentaireService.modifier(getUserId(auth), id, dto)));
    }

    // DELETE /api/sessions/{sessionId}/commentaires/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> supprimer(
            @PathVariable Long sessionId,
            @PathVariable Long id,
            Authentication auth) {
        commentaireService.supprimer(getUserId(auth), id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    private Long getUserId(Authentication auth) {
        return ((Utilisateur) auth.getPrincipal()).getId();
    }
}