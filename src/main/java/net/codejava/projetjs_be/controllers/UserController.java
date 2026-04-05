package net.codejava.projetjs_be.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.codejava.projetjs_be.dto.*;
import net.codejava.projetjs_be.entities.Utilisateur;
import net.codejava.projetjs_be.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // GET /api/user/profil
    @GetMapping("/profil")
    public ResponseEntity<ApiResponse<UtilisateurDTO>> getMonProfil(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(
                userService.getMonProfil(getUserId(auth))));
    }

    // PUT /api/user/profil
    @PutMapping("/profil")
    public ResponseEntity<ApiResponse<UtilisateurDTO>> modifierProfil(
            @Valid @RequestBody UpdateProfilDTO dto,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(
                userService.modifierProfil(getUserId(auth), dto)));
    }

    // PATCH /api/user/mot-de-passe
    @PatchMapping("/mot-de-passe")
    public ResponseEntity<ApiResponse<Void>> changerMotDePasse(
            @Valid @RequestBody ChangeMotDePasseDTO dto,
            Authentication auth) {
        userService.changerMotDePasse(getUserId(auth), dto);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // DELETE /api/user/compte
    @DeleteMapping("/compte")
    public ResponseEntity<ApiResponse<Void>> supprimerCompte(Authentication auth) {
        userService.supprimerCompte(getUserId(auth));
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    private Long getUserId(Authentication auth) {
        Utilisateur u = (Utilisateur) auth.getPrincipal();
        return u.getId();
    }
}
