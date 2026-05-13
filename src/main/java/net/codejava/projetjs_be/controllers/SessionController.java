package net.codejava.projetjs_be.controllers;

import lombok.RequiredArgsConstructor;
import net.codejava.projetjs_be.dto.*;
import net.codejava.projetjs_be.entities.Utilisateur;
import net.codejava.projetjs_be.repositories.UtilisateurRepository;
import net.codejava.projetjs_be.services.SessionService;
import net.codejava.projetjs_be.services.PlanificateurService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService      sessionService;
    private final PlanificateurService planificateurService;
    private final UtilisateurRepository utilisateurRepository;

    private Long getUserId(Authentication auth) {
        return ((Utilisateur) auth.getPrincipal()).getId();
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SessionDTO>>> getMesSessions(
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(
                sessionService.getMesSessions(getUserId(auth))));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SessionDTO>> creer(
            @RequestBody SessionDTO dto,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(
                sessionService.creer(getUserId(auth), dto)));
    }

    @PatchMapping("/{id}/demarrer")
    public ResponseEntity<ApiResponse<SessionDTO>> demarrer(
            @PathVariable Long id,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(
                sessionService.demarrer(getUserId(auth), id)));
    }

    @PatchMapping("/{id}/terminer")
    public ResponseEntity<ApiResponse<SessionDTO>> terminer(
            @PathVariable Long id,
            @RequestParam int dureeReelleMin,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(
                sessionService.terminer(getUserId(auth), id, dureeReelleMin)));
    }

    @PatchMapping("/{id}/annuler")
    public ResponseEntity<ApiResponse<SessionDTO>> annuler(
            @PathVariable Long id,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(
                sessionService.annuler(getUserId(auth), id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> supprimer(
            @PathVariable Long id,
            Authentication auth) {
        sessionService.supprimer(getUserId(auth), id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PostMapping("/planifier")
    public ResponseEntity<ApiResponse<List<SessionDTO>>> planifier(
            @RequestParam String semaine,
            @RequestParam(defaultValue = "90") int dureeMax,
            Authentication auth) {

        Utilisateur u = utilisateurRepository.findByEmail(auth.getName())
                .orElseThrow();
        LocalDate date = LocalDate.parse(semaine);

        // Convertir heures → minutes (frontend envoie en heures ex: 2.0)
        int dureeMin = (int)(dureeMax * 60);

        List<SessionDTO> result = planificateurService.genererPlanning(
            u.getId(), date, dureeMin
        );
        return ResponseEntity.ok(ApiResponse.ok(result));
    }
}