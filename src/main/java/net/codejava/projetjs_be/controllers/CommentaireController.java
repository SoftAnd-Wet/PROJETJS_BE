package net.codejava.projetjs_be.controllers;

import lombok.RequiredArgsConstructor;
import net.codejava.projetjs_be.dto.*;
import net.codejava.projetjs_be.entities.Utilisateur;
import net.codejava.projetjs_be.services.CommentaireService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sessions/{sessionId}/commentaires")
@RequiredArgsConstructor
public class CommentaireController {

    private final CommentaireService commentaireService;

    // GET /api/sessions/{sessionId}/commentaires
    @GetMapping
    public ResponseEntity<ApiResponse<List<CommentaireDTO>>> getCommentaires(
            @PathVariable Long sessionId,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(
                commentaireService.getCommentaires(getUserId(auth), sessionId)));
    }

    // POST /api/sessions/{sessionId}/commentaires
    @PostMapping
    public ResponseEntity<ApiResponse<CommentaireDTO>> ajouter(
            @PathVariable Long sessionId,
            @RequestBody Map<String, String> body,
            Authentication auth) {
        String contenu = body.get("contenu");
        return ResponseEntity.ok(ApiResponse.ok(
                commentaireService.ajouter(getUserId(auth), sessionId, contenu)));
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