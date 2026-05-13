package net.codejava.projetjs_be.services;

import lombok.RequiredArgsConstructor;
import net.codejava.projetjs_be.dto.NotificationDTO;
import net.codejava.projetjs_be.entities.*;
import net.codejava.projetjs_be.enums.TypeNotif;
import net.codejava.projetjs_be.repositories.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SessionRepository      sessionRepository;

    // ── Rappel 30 min avant chaque session planifiée ──
    @Scheduled(fixedRate = 60000) // toutes les minutes
    public void envoyerRappelsSessions() {
        LocalDateTime maintenant = LocalDateTime.now();
        LocalDateTime dans30min  = maintenant.plusMinutes(30);

        // Sessions qui commencent dans ~30 min
        List<SessionEtude> sessions = sessionRepository
                .findSessionsDebutantEntre(maintenant.plusMinutes(28), dans30min);

        for (SessionEtude s : sessions) {
            // Vérifier qu'on n'a pas déjà envoyé ce rappel
            boolean dejaEnvoye = notificationRepository
                    .existsByDestinataireIdAndTypeAndMessage(
                        s.getUtilisateur().getId(),
                        TypeNotif.RAPPEL_SESSION,
                        "Rappel : votre session " +
                            (s.getMatiere() != null ? s.getMatiere().getNom() : "libre") +
                            " commence dans 30 minutes"
                    );

            if (!dejaEnvoye) {
                creerNotification(
                    s.getUtilisateur(),
                    TypeNotif.RAPPEL_SESSION,
                    "Rappel : votre session " +
                        (s.getMatiere() != null ? s.getMatiere().getNom() : "libre") +
                        " commence dans 30 minutes"
                );
            }
        }
    }

    // ── Vérifier objectifs atteints après chaque session terminée ──
    public void verifierObjectifAtteint(SessionEtude session) {
        if (session.getMatiere() == null) return;

        Matiere m = session.getMatiere();
        if (m.getProgression() >= 100f) {
            boolean dejaNotifie = notificationRepository
                    .existsByDestinataireIdAndTypeAndMessage(
                        session.getUtilisateur().getId(),
                        TypeNotif.OBJECTIF_ATTEINT,
                        "Objectif atteint pour " + m.getNom() + " !"
                    );

            if (!dejaNotifie) {
                creerNotification(
                    session.getUtilisateur(),
                    TypeNotif.OBJECTIF_ATTEINT,
                    "Objectif atteint pour " + m.getNom() + " !"
                );
            }
        }
    }

    public void creerNotification(Utilisateur destinataire, TypeNotif type, String message) {
        Notification n = Notification.builder()
                .destinataire(destinataire)
                .type(type)
                .message(message)
                .lue(false)
                .envoyeeLe(LocalDateTime.now())
                .build();
        notificationRepository.save(n);
    }

    public List<NotificationDTO> getMesNotifications(Long userId) {
        return notificationRepository
                .findByDestinataireIdOrderByEnvoyeeLeDesc(userId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<NotificationDTO> getMesNotifsNonLues(Long userId) {
        return notificationRepository
                .findByDestinataireIdAndLueFalseOrderByEnvoyeeLeDesc(userId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public long countNonLues(Long userId) {
        return notificationRepository.countByDestinataireIdAndLueFalse(userId);
    }

    public void marquerLue(Long userId, Long notifId) {
        Notification n = notificationRepository.findById(notifId)
                .orElseThrow(() -> new RuntimeException("Notification non trouvée"));
        if (!n.getDestinataire().getId().equals(userId))
            throw new RuntimeException("Accès refusé");
        n.setLue(true);
        notificationRepository.save(n);
    }

    public void marquerToutesLues(Long userId) {
        List<Notification> notifs = notificationRepository
                .findByDestinataireIdAndLueFalseOrderByEnvoyeeLeDesc(userId);
        notifs.forEach(n -> n.setLue(true));
        notificationRepository.saveAll(notifs);
    }

    private NotificationDTO toDTO(Notification n) {
        return NotificationDTO.builder()
                .id(n.getId())
                .message(n.getMessage())
                .type(n.getType())
                .lue(n.isLue())
                .envoyeeLe(n.getEnvoyeeLe())
                .build();
    }
}