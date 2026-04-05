package net.codejava.projetjs_be.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import net.codejava.projetjs_be.dto.SessionDTO;
import net.codejava.projetjs_be.entities.Utilisateur;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import net.codejava.projetjs_be.dto.ApiResponse;
import net.codejava.projetjs_be.dto.GroupeDTO;
import net.codejava.projetjs_be.dto.MembreDTO;
import net.codejava.projetjs_be.services.GroupeService;

@RestController
@RequestMapping("/api/groupes")
@RequiredArgsConstructor
public class GroupeController {

    private final GroupeService groupeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<GroupeDTO>>> getMesGroupes(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(
                groupeService.getMesGroupes(getUserId(auth))));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GroupeDTO>> creer(
            @Valid @RequestBody GroupeDTO dto, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(
                groupeService.creer(getUserId(auth), dto)));
    }

    @PostMapping("/{id}/inviter")
    public ResponseEntity<ApiResponse<Void>> inviter(
            @PathVariable Long id,
            @RequestParam String email, Authentication auth) {
        groupeService.inviterMembre(getUserId(auth), id, email);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @DeleteMapping("/{id}/quitter")
    public ResponseEntity<ApiResponse<Void>> quitter(
            @PathVariable Long id, Authentication auth) {
        groupeService.quitterGroupe(getUserId(auth), id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @GetMapping("/{id}/sessions")
    public ResponseEntity<ApiResponse<List<SessionDTO>>> getSessions(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(
                groupeService.getSessionsGroupe(id)));
    }

    private Long getUserId(Authentication auth) {
        return ((Utilisateur) auth.getPrincipal()).getId();
    }

    @GetMapping("/{id}/membres")
    public ResponseEntity<ApiResponse<List<MembreDTO>>> getMembres(
            @PathVariable Long id,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(
                groupeService.getMembresGroupe(getUserId(auth), id)));
    }
}
