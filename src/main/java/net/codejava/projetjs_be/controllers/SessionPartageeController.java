package net.codejava.projetjs_be.controllers;

import lombok.RequiredArgsConstructor;
import net.codejava.projetjs_be.dto.*;
import net.codejava.projetjs_be.entities.Utilisateur;
import net.codejava.projetjs_be.services.SessionPartageeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/partages")
@RequiredArgsConstructor
public class SessionPartageeController {

    private final SessionPartageeService sessionPartageeService;

    // POST /api/partages?sessionId=1&groupeId=2
    @PostMapping
    public ResponseEntity<ApiResponse<SessionPartageeDTO>> partager(
            @RequestParam Long sessionId,
            @RequestParam Long groupeId,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(
                sessionPartageeService.partager(getUserId(auth), sessionId, groupeId)));
    }

    // GET /api/partages/groupe/{groupeId}
    @GetMapping("/groupe/{groupeId}")
    public ResponseEntity<ApiResponse<List<SessionPartageeDTO>>> getSessionsPartagees(
            @PathVariable Long groupeId,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(
                sessionPartageeService.getSessionsPartagees(getUserId(auth), groupeId)));
    }

    // DELETE /api/partages/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> retirerPartage(
            @PathVariable Long id,
            Authentication auth) {
        sessionPartageeService.retirerPartage(getUserId(auth), id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    private Long getUserId(Authentication auth) {
        return ((Utilisateur) auth.getPrincipal()).getId();
    }
}