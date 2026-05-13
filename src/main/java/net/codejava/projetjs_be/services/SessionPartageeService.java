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
    private final SessionRepository         sessionRepository;
    private final GroupeRepository          groupeRepository;
    private final MembreRepository          membreRepository;

    public SessionPartageeDTO partager(Long userId, Long sessionId, Long groupeId) {

        if (!membreRepository.existsByUtilisateurIdAndGroupeId(userId, groupeId)) {
            throw new RuntimeException("Vous n'êtes pas membre de ce groupe");
        }

        SessionEtude session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session non trouvée"));
        if (!session.getUtilisateur().getId().equals(userId)) {
            throw new RuntimeException("Cette session ne vous appartient pas");
        }

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

    public List<SessionPartageeDTO> getSessionsPartagees(Long userId, Long groupeId) {
        if (!membreRepository.existsByUtilisateurIdAndGroupeId(userId, groupeId)) {
            throw new RuntimeException("Vous n'êtes pas membre de ce groupe");
        }
        return sessionPartageeRepository.findByGroupeId(groupeId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public void retirerPartage(Long userId, Long id) {
        SessionPartagee sp = sessionPartageeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session partagée non trouvée"));
        if (!sp.getSession().getUtilisateur().getId().equals(userId)) {
            throw new RuntimeException("Accès refusé");
        }
        sessionPartageeRepository.delete(sp);
    }

    private SessionPartageeDTO toDTO(SessionPartagee sp) {
        SessionEtude s = sp.getSession();

        // ✅ SOLUTION : priorité nom > matiereNom > "Session"
        String sessionNom = s.getNom();
        String matiereNom = s.getMatiere() != null ? s.getMatiere().getNom() : null;

        // ✅ Calculer dureePrevueMin depuis debut/fin si = 0
        int dureePrevueMin = s.getDureePrevueMin();
        if (dureePrevueMin <= 0 && s.getDebut() != null && s.getFin() != null) {
            dureePrevueMin = (int) java.time.Duration.between(
                s.getDebut(), s.getFin()
            ).toMinutes();
        }

        return SessionPartageeDTO.builder()
                .id(sp.getId())
                .sessionId(s.getId())           
                .sessionNom(sessionNom)        
                .sessionMatiereNom(matiereNom)  
                .sessionDebut(s.getDebut())     
                .sessionFin(s.getFin())         
                .sessionDureePrevueMin(dureePrevueMin)
                .sessionStatut(s.getStatut() != null ? s.getStatut().name() : null)
                .groupeId(sp.getGroupe().getId())
                .groupeNom(sp.getGroupe().getNom())
                .partageLe(sp.getPartageLe())
                .build();
    }
}