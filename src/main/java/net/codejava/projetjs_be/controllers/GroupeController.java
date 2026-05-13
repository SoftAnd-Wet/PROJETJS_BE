package net.codejava.projetjs_be.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import net.codejava.projetjs_be.dto.ApiResponse;
import net.codejava.projetjs_be.dto.GroupeDTO;
import net.codejava.projetjs_be.dto.MembreDTO;
import net.codejava.projetjs_be.dto.SessionPartageeDTO;
import net.codejava.projetjs_be.entities.Utilisateur;
import net.codejava.projetjs_be.services.GroupeService;
import net.codejava.projetjs_be.services.SessionPartageeService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groupes")
@RequiredArgsConstructor
public class GroupeController {

    private final GroupeService          groupeService;
    private final SessionPartageeService sessionPartageeService; // ✅ injecté

    @GetMapping
    public ResponseEntity<ApiResponse<List<GroupeDTO>>> getMesGroupes(
            Authentication auth) {
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
            @RequestParam String email,
            Authentication auth) {
        groupeService.inviterMembre(getUserId(auth), id, email);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @DeleteMapping("/{id}/quitter")
    public ResponseEntity<ApiResponse<Void>> quitter(
            @PathVariable Long id, Authentication auth) {
        groupeService.quitterGroupe(getUserId(auth), id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // ✅ FIX : retourne SessionPartageeDTO avec sessionId, sessionNom,
    // sessionDebut, sessionFin — tout ce dont Angular a besoin
    @GetMapping("/{id}/sessions")
    public ResponseEntity<ApiResponse<List<SessionPartageeDTO>>> getSessions(
            @PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(
                sessionPartageeService.getSessionsPartagees(getUserId(auth), id)));
    }

    @GetMapping("/{id}/membres")
    public ResponseEntity<ApiResponse<List<MembreDTO>>> getMembres(
            @PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(
                groupeService.getMembresGroupe(getUserId(auth), id)));
    }

    private Long getUserId(Authentication auth) {
        return ((Utilisateur) auth.getPrincipal()).getId();
    }
}