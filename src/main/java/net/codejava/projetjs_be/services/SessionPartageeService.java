package net.codejava.projetjs_be.services;

import lombok.RequiredArgsConstructor;
import net.codejava.projetjs_be.dto.SessionPartageeDTO;
import net.codejava.projetjs_be.entities.*;
import net.codejava.projetjs_be.repositories.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionPartageeService {

    private final SessionPartageeRepository sessionPartageeRepository;
    private final SessionRepository sessionRepository;
    private final GroupeRepository groupeRepository;
    private final MembreRepository membreRepository;

    // Partager une session dans un groupe
    public SessionPartageeDTO partager(Long userId, Long sessionId, Long groupeId) {

        // Vérifier que l'utilisateur est membre du groupe
        if (!membreRepository.existsByUtilisateurIdAndGroupeId(userId, groupeId)) {
            throw new RuntimeException("Vous n'êtes pas membre de ce groupe");
        }

        // Vérifier que la session appartient à l'utilisateur
        SessionEtude session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session non trouvée"));
        if (!session.getUtilisateur().getId().equals(userId)) {
            throw new RuntimeException("Cette session ne vous appartient pas");
        }

        // Vérifier que ce n'est pas déjà partagé
        if (sessionPartageeRepository.existsBySessionIdAndGroupeId(sessionId, groupeId)) {
            throw new RuntimeException("Session déjà partagée dans ce groupe");
        }

        GroupeEtude groupe = groupeRepository.findById(groupeId)
                .orElseThrow(() -> new RuntimeException("Groupe non trouvé"));

        SessionPartagee sp = SessionPartagee.builder()
                .session(session)
                .groupe(groupe)
                .build();

        return toDTO(sessionPartageeRepository.save(sp));
    }

    // Voir toutes les sessions partagées d'un groupe
    public List<SessionPartageeDTO> getSessionsPartagees(Long userId, Long groupeId) {
        if (!membreRepository.existsByUtilisateurIdAndGroupeId(userId, groupeId)) {
            throw new RuntimeException("Vous n'êtes pas membre de ce groupe");
        }
        return sessionPartageeRepository.findByGroupeId(groupeId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Retirer une session partagée
    public void retirerPartage(Long userId, Long id) {
        SessionPartagee sp = sessionPartageeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session partagée non trouvée"));

        // Seul le propriétaire de la session peut retirer le partage
        if (!sp.getSession().getUtilisateur().getId().equals(userId)) {
            throw new RuntimeException("Accès refusé");
        }
        sessionPartageeRepository.delete(sp);
    }

    private SessionPartageeDTO toDTO(SessionPartagee sp) {
        return SessionPartageeDTO.builder()
                .id(sp.getId())
                .sessionId(sp.getSession().getId())
                .sessionMatiereNom(sp.getSession().getMatiere() != null
                        ? sp.getSession().getMatiere().getNom() : null)
                .sessionDebut(sp.getSession().getDebut())
                .sessionFin(sp.getSession().getFin())
                .groupeId(sp.getGroupe().getId())
                .groupeNom(sp.getGroupe().getNom())
                .partageLe(sp.getPartageLe())
                .build();
    }
}