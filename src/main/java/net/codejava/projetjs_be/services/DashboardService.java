package net.codejava.projetjs_be.services;

import lombok.RequiredArgsConstructor;
import net.codejava.projetjs_be.entities.SessionEtude;
import net.codejava.projetjs_be.enums.StatutSession;
import net.codejava.projetjs_be.repositories.*;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final SessionRepository  sessionRepository;
    private final MatiereRepository  matiereRepository;

    public Map<String, Object> getDashboard(Long userId) {
        Map<String, Object> stats = new HashMap<>();

        // ── Temps total étudié toutes sessions ──
        Long totalGeneral = sessionRepository.sumDureeReelleByUserId(userId);
        stats.put("tempsTotalMin", totalGeneral != null ? totalGeneral : 0);

        // ── Sessions de la semaine ──
        LocalDateTime debutSemaine = LocalDate.now()
                .with(DayOfWeek.MONDAY).atStartOfDay();
        LocalDateTime finSemaine = debutSemaine.plusDays(7);

        List<SessionEtude> sessionsSemaine = sessionRepository
                .findByUtilisateurIdAndDebutBetween(userId, debutSemaine, finSemaine);

        stats.put("sessionsCetteSemaine", sessionsSemaine.size());

        // ── Temps étudié CETTE semaine ──
        long minutesSemaine = sessionsSemaine.stream()
                .filter(SessionEtude::isCompletee)
                .mapToLong(SessionEtude::getDureeReelleMin)
                .sum();
        stats.put("tempsSemainMin", minutesSemaine);

        // ── Complétées vs planifiées ──
        long completees = sessionRepository
                .findByUtilisateurIdAndStatut(userId, StatutSession.TERMINEE).size();
        long planifiees = sessionRepository
                .findByUtilisateurIdAndStatut(userId, StatutSession.PLANIFIEE).size();
        stats.put("sessionsCompletees", completees);
        stats.put("sessionsPlanifiees", planifiees);

        // ── Progression par matière ──
        var progressions = matiereRepository
                .findByUtilisateurIdOrderByPrioriteAsc(userId)
                .stream()
                .map(m -> Map.of(
                        "nom",        m.getNom(),
                        "progression", m.getProgression(),
                        "objectif",   m.getObjectifHebdoHeures()
                )).toList();
        stats.put("progressionMatieres", progressions);

        return stats;
    }
}