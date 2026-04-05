package net.codejava.projetjs_be.controllers;

import lombok.RequiredArgsConstructor;
import net.codejava.projetjs_be.dto.*;
import net.codejava.projetjs_be.entities.Utilisateur;
import net.codejava.projetjs_be.services.InvitationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invitations")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    private Long getUserId(Authentication auth) {
        return ((Utilisateur) auth.getPrincipal()).getId();
    }

    // Envoyer une invitation
    @PostMapping
    public ResponseEntity<ApiResponse<InvitationDTO>> envoyerInvitation(
            @RequestParam Long groupeId,
            @RequestParam String email,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(
                invitationService.envoyerInvitation(
                        getUserId(auth), groupeId, email)));
    }

    // Voir toutes mes invitations reçues
    @GetMapping
    public ResponseEntity<ApiResponse<List<InvitationDTO>>> getMesInvitations(
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(
                invitationService.getMesInvitations(getUserId(auth))));
    }

    // Voir mes invitations en attente uniquement
    @GetMapping("/en-attente")
    public ResponseEntity<ApiResponse<List<InvitationDTO>>> getMesInvitationsEnAttente(
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(
                invitationService.getMesInvitationsEnAttente(getUserId(auth))));
    }

    // Accepter une invitation
    @PatchMapping("/{id}/accepter")
    public ResponseEntity<ApiResponse<Void>> accepter(
            @PathVariable Long id,
            Authentication auth) {
        invitationService.accepterInvitation(getUserId(auth), id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // Refuser une invitation
    @PatchMapping("/{id}/refuser")
    public ResponseEntity<ApiResponse<Void>> refuser(
            @PathVariable Long id,
            Authentication auth) {
        invitationService.refuserInvitation(getUserId(auth), id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}