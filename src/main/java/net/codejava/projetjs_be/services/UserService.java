package net.codejava.projetjs_be.services;

import lombok.RequiredArgsConstructor;
import net.codejava.projetjs_be.dto.*;
import net.codejava.projetjs_be.entities.Utilisateur;
import net.codejava.projetjs_be.repositories.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    // Récupérer mon profil
    public UtilisateurDTO getMonProfil(Long userId) {
        Utilisateur u = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return toDTO(u);
    }

    // Modifier nom et email
    public UtilisateurDTO modifierProfil(Long userId, UpdateProfilDTO dto) {
        Utilisateur u = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Vérifier si le nouvel email est déjà pris par quelqu'un d'autre
        if (!u.getEmail().equals(dto.getEmail()) &&
                utilisateurRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }

        u.setNom(dto.getNom());
        u.setEmail(dto.getEmail());
        return toDTO(utilisateurRepository.save(u));
    }

    // Changer mot de passe
    public void changerMotDePasse(Long userId, ChangeMotDePasseDTO dto) {
        Utilisateur u = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Vérifier l'ancien mot de passe
        if (!passwordEncoder.matches(dto.getAncienMotDePasse(), u.getMotDePasse())) {
            throw new RuntimeException("Ancien mot de passe incorrect");
        }

        u.setMotDePasse(passwordEncoder.encode(dto.getNouveauMotDePasse()));
        utilisateurRepository.save(u);
    }

    // Supprimer son compte
    public void supprimerCompte(Long userId) {
        Utilisateur u = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        utilisateurRepository.delete(u);
    }

    private UtilisateurDTO toDTO(Utilisateur u) {
        return UtilisateurDTO.builder()
                .id(u.getId())
                .nom(u.getNom())
                .email(u.getEmail())
                .role(u.getRole())
                .dateInscription(u.getDateInscription())
                .build();
    }
}