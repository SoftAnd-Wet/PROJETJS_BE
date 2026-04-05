package net.codejava.projetjs_be.controllers;

import lombok.RequiredArgsConstructor;
import net.codejava.projetjs_be.dto.*;
import net.codejava.projetjs_be.entities.Utilisateur;
import net.codejava.projetjs_be.services.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/groupes/{groupeId}/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    private Long getUserId(Authentication auth) {
        return ((Utilisateur) auth.getPrincipal()).getId();
    }

    // GET /api/groupes/{groupeId}/messages
    @GetMapping
    public ResponseEntity<ApiResponse<List<MessageDTO>>> getMessages(
            @PathVariable Long groupeId,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(
                messageService.getMessages(getUserId(auth), groupeId)));
    }

    // POST /api/groupes/{groupeId}/messages
    @PostMapping
    public ResponseEntity<ApiResponse<MessageDTO>> envoyerMessage(
            @PathVariable Long groupeId,
            @RequestBody Map<String, String> body,
            Authentication auth) {
        String contenu = body.get("contenu");
        return ResponseEntity.ok(ApiResponse.ok(
                messageService.envoyerMessage(getUserId(auth), groupeId, contenu)));
    }

    // DELETE /api/groupes/{groupeId}/messages/{messageId}
    @DeleteMapping("/{messageId}")
    public ResponseEntity<ApiResponse<Void>> supprimerMessage(
            @PathVariable Long groupeId,
            @PathVariable Long messageId,
            Authentication auth) {
        messageService.supprimerMessage(getUserId(auth), messageId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}