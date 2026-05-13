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

    private final SessionRepository sessionRepository;
    private final MatiereRepository matiereRepository;

    public Map<String, Object> getDashboard(Long userId) {
        Map<String, Object> stats = new HashMap<>();

        // ── Temps total étudié toutes sessions ──
        Long totalGeneral = sessionRepository.sumDureeReelleByUserId(userId);
        stats.put("tempsTotalMin", totalGeneral != null ? totalGeneral : 0);

        // ── Aujourd'hui ──
        LocalDateTime debutAujourd = LocalDate.now().atStartOfDay();
        LocalDateTime finAujourd   = debutAujourd.plusDays(1);

        List<SessionEtude> sessionsAujourd = sessionRepository
                .findByUtilisateurIdAndDebutBetween(userId, debutAujourd, finAujourd);

        long minutesAujourd = sessionsAujourd.stream()
                .filter(SessionEtude::isCompletee)
                .mapToLong(s -> s.getDureeReelleMin() > 0
                        ? s.getDureeReelleMin()
                        : s.getDureePrevueMin())
                .sum();

        stats.put("tempsAujourdMin",   minutesAujourd);
        stats.put("sessionsAujourdNb", sessionsAujourd.size());

        // ── Sessions de la semaine ──
        LocalDateTime debutSemaine = LocalDate.now()
                .with(DayOfWeek.MONDAY).atStartOfDay();
        LocalDateTime finSemaine   = debutSemaine.plusDays(7);

        List<SessionEtude> sessionsSemaine = sessionRepository
                .findByUtilisateurIdAndDebutBetween(userId, debutSemaine, finSemaine);

        stats.put("sessionsCetteSemaine", sessionsSemaine.size());

        // ── Temps étudié CETTE semaine ──
        long minutesSemaine = sessionsSemaine.stream()
                .filter(SessionEtude::isCompletee)
                .mapToLong(s -> s.getDureeReelleMin() > 0
                        ? s.getDureeReelleMin()
                        : s.getDureePrevueMin())
                .sum();
        stats.put("tempsSemainMin", minutesSemaine);

        // ── Ce mois ──
        LocalDateTime debutMois = LocalDate.now()
                .withDayOfMonth(1).atStartOfDay();
        List<SessionEtude> sessionsMois = sessionRepository
                .findByUtilisateurIdAndDebutBetween(userId, debutMois, finAujourd);
        long minutesMois = sessionsMois.stream()
                .filter(SessionEtude::isCompletee)
                .mapToLong(s -> s.getDureeReelleMin() > 0
                        ? s.getDureeReelleMin()
                        : s.getDureePrevueMin())
                .sum();
        stats.put("tempsMoisMin", minutesMois);

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
                        "nom",         m.getNom(),
                        "progression", m.getProgression(),
                        "objectif",    m.getObjectifHebdoHeures()
                )).toList();
        stats.put("progressionMatieres", progressions);

        return stats;
    }
}