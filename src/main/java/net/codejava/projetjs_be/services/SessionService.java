package net.codejava.projetjs_be.services;

import lombok.RequiredArgsConstructor;
import net.codejava.projetjs_be.dto.SessionDTO;
import net.codejava.projetjs_be.entities.*;
import net.codejava.projetjs_be.enums.StatutSession;
import net.codejava.projetjs_be.repositories.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository     sessionRepository;
    private final MatiereRepository     matiereRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final NotificationService   notificationService;

    public List<SessionDTO> getMesSessions(Long userId) {
        return sessionRepository.findByUtilisateurId(userId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public SessionDTO creer(Long userId, SessionDTO dto) {
        Utilisateur u = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        SessionEtude.SessionEtudeBuilder builder = SessionEtude.builder()
                .debut(dto.getDebut())
                .fin(dto.getFin())
                .dureePrevueMin(dto.getDureePrevueMin() > 0 ? dto.getDureePrevueMin() : 60)
                .statut(StatutSession.PLANIFIEE)
                .completee(false)
                .utilisateur(u);

        // ✅ SOLUTION : stocker le nom libre directement sur la session
        if (dto.getNom() != null && !dto.getNom().isBlank()) {
            builder.nom(dto.getNom().trim());
        }

        if (dto.getMatiereId() != null) {
            Matiere m = matiereRepository.findById(dto.getMatiereId())
                    .orElseThrow(() -> new RuntimeException("Matière non trouvée"));
            builder.matiere(m);
        }

        return toDTO(sessionRepository.save(builder.build()));
    }

    public SessionDTO demarrer(Long userId, Long id) {
        SessionEtude s = getSessionVerifiee(userId, id);
        s.setStatut(StatutSession.EN_COURS);
        s.setDebut(LocalDateTime.now());
        return toDTO(sessionRepository.save(s));
    }

    public SessionDTO terminer(Long userId, Long id, int dureeReelleMin) {
        SessionEtude s = getSessionVerifiee(userId, id);
        s.setStatut(StatutSession.TERMINEE);
        s.setFin(LocalDateTime.now());
        s.setDureeReelleMin(dureeReelleMin);
        s.setCompletee(true);

        if (s.getMatiere() != null) {
            Matiere m = s.getMatiere();
            float nouvelleProgression = Math.min(100f,
                    m.getProgression() + (dureeReelleMin / 60f
                            / Math.max(m.getObjectifHebdoHeures(), 1) * 100));
            m.setProgression(nouvelleProgression);
            matiereRepository.save(m);
        }

        SessionDTO result = toDTO(sessionRepository.save(s));
        notificationService.verifierObjectifAtteint(s);
        return result;
    }

    public SessionDTO annuler(Long userId, Long id) {
        SessionEtude s = getSessionVerifiee(userId, id);
        s.setStatut(StatutSession.ANNULEE);
        return toDTO(sessionRepository.save(s));
    }

    public void supprimer(Long userId, Long id) {
        SessionEtude s = getSessionVerifiee(userId, id);
        sessionRepository.delete(s);
    }

    private SessionEtude getSessionVerifiee(Long userId, Long id) {
        SessionEtude s = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session non trouvée"));
        if (!s.getUtilisateur().getId().equals(userId))
            throw new RuntimeException("Accès refusé");
        return s;
    }

    public SessionDTO toDTO(SessionEtude s) {
        //Résoudre le meilleur nom disponible
        String matiereNom = s.getMatiere() != null
                ? s.getMatiere().getNom()
                : (s.getNom() != null ? s.getNom() : "Session libre");

        //Calculer dureePrevueMin si = 0
        int dureePrevueMin = s.getDureePrevueMin();
        if (dureePrevueMin <= 0 && s.getDebut() != null && s.getFin() != null) {
            dureePrevueMin = (int) java.time.Duration.between(
                s.getDebut(), s.getFin()
            ).toMinutes();
        }

        return SessionDTO.builder()
                .id(s.getId())
                .nom(s.getNom())                   
                .debut(s.getDebut())
                .fin(s.getFin())
                .dureePrevueMin(dureePrevueMin)    
                .dureeReelleMin(s.getDureeReelleMin())
                .statut(s.getStatut())
                .completee(s.isCompletee())
                .matiereId(s.getMatiere() != null ? s.getMatiere().getId() : null)
                .matiereNom(matiereNom)
                .build();
    }
}