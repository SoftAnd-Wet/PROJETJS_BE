package net.codejava.projetjs_be.services;

import lombok.RequiredArgsConstructor;
import net.codejava.projetjs_be.dto.SessionDTO;
import net.codejava.projetjs_be.entities.*;
import net.codejava.projetjs_be.enums.StatutSession;
import net.codejava.projetjs_be.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanificateurService {

    private final MatiereRepository         matiereRepository;
    private final DisponibiliteRepository   disponibiliteRepository;
    private final SessionRepository         sessionRepository;
    private final UtilisateurRepository     utilisateurRepository;
    private final SessionPartageeRepository sessionPartageeRepository;

    private static final int PAUSE_MIN = 10;

    @Transactional
    public List<SessionDTO> genererPlanning(Long userId, LocalDate semaine, int dureeSessionMin) {

        int dureeArrondie = Math.max(60, (dureeSessionMin / 60) * 60);

        LocalDate lundi = semaine.with(
            TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
        );

        List<Matiere> matieres = matiereRepository
                .findByUtilisateurIdOrderByPrioriteAsc(userId);
        List<Disponibilite> dispos = disponibiliteRepository
                .findByUtilisateurId(userId);

        if (matieres.isEmpty() || dispos.isEmpty()) return List.of();

        LocalDateTime debutSem = lundi.atStartOfDay();
        LocalDateTime finSem   = lundi.plusDays(7).atStartOfDay();

        // ── Étape 1 : Supprimer toutes les sessions personnelles PLANIFIEE ──
        // (sessions dont l'utilisateur est propriétaire ET non partagées)
        List<SessionEtude> toutesPersonnelles = sessionRepository
                .findByUtilisateurIdAndStatut(userId, StatutSession.PLANIFIEE);

        Set<Long> idsDejaPartages = new HashSet<>();
        for (SessionEtude s : toutesPersonnelles) {
            if (!sessionPartageeRepository.findBySessionId(s.getId()).isEmpty()) {
                idsDejaPartages.add(s.getId());
            }
        }

        List<SessionEtude> aSupprimer = toutesPersonnelles.stream()
                .filter(s -> !idsDejaPartages.contains(s.getId()))
                .collect(Collectors.toList());

        if (!aSupprimer.isEmpty()) {
            List<Long> ids = aSupprimer.stream()
                    .map(SessionEtude::getId)
                    .collect(Collectors.toList());
            sessionPartageeRepository.deleteBySessionIdIn(ids);
            sessionRepository.deleteAll(aSupprimer);
        }

        // ── Étape 2 : Récupérer TOUTES les sessions de groupe de la semaine ──
        // via sessions_partagees pour tous les groupes dont l'user est membre
        // (sessions créées par n'importe quel membre, pas seulement par l'user)
        List<SessionPartagee> toutesPartagees = sessionPartageeRepository
                .findByUtilisateurId(userId);

        List<LocalDateTime[]> creneauxOccupes = new ArrayList<>();
        for (SessionPartagee sp : toutesPartagees) {
            SessionEtude s = sp.getSession();
            if (s.getDebut() == null || s.getFin() == null) continue;
            // Filtrer : seulement la semaine ciblée
            if (s.getDebut().isBefore(debutSem) || !s.getDebut().isBefore(finSem)) continue;
            creneauxOccupes.add(new LocalDateTime[]{s.getDebut(), s.getFin()});
        }

        // ── Étape 3 : Générer les créneaux disponibles ──
        List<LocalDateTime[]> creneaux = genererCreneaux(lundi, dispos, dureeArrondie);
        if (creneaux.isEmpty()) return List.of();

        Utilisateur u = utilisateurRepository.findById(userId).orElseThrow();

        // File matières selon objectif et priorité
        List<Matiere> file = new ArrayList<>();
        for (Matiere m : matieres) {
            int nb = Math.max(1, Math.round(m.getObjectifHebdoHeures() * 60f / dureeArrondie));
            for (int i = 0; i < nb; i++) file.add(m);
        }

        // ── Étape 4 : Créer les sessions personnelles sans chevauchement ──
        List<SessionEtude> sessions = new ArrayList<>();
        for (int i = 0; i < Math.min(file.size(), creneaux.size()); i++) {
            LocalDateTime[] cr = creneaux.get(i);
            if (seChevauchent(cr, creneauxOccupes)) continue;

            SessionEtude s = SessionEtude.builder()
                    .debut(cr[0])
                    .fin(cr[1])
                    .dureePrevueMin(dureeArrondie)
                    .statut(StatutSession.PLANIFIEE)
                    .completee(false)
                    .utilisateur(u)
                    .matiere(file.get(i))
                    .build();
            sessions.add(s);
            creneauxOccupes.add(cr);
        }

        List<SessionEtude> saved = sessionRepository.saveAll(sessions);

        return saved.stream().map(s -> SessionDTO.builder()
                .id(s.getId())
                .debut(s.getDebut())
                .fin(s.getFin())
                .dureePrevueMin(s.getDureePrevueMin())
                .statut(s.getStatut())
                .matiereId(s.getMatiere().getId())
                .matiereNom(s.getMatiere().getNom())
                .build())
                .collect(Collectors.toList());
    }

    private boolean seChevauchent(LocalDateTime[] cr, List<LocalDateTime[]> occupes) {
        for (LocalDateTime[] occ : occupes) {
            if (cr[0].isBefore(occ[1]) && cr[1].isAfter(occ[0])) return true;
        }
        return false;
    }

    private List<LocalDateTime[]> genererCreneaux(
            LocalDate lundi, List<Disponibilite> dispos, int dureeMin) {

        List<LocalDateTime[]> creneaux = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            LocalDate jour = lundi.plusDays(i);
            DayOfWeek dow  = jour.getDayOfWeek();

            for (Disponibilite d : dispos) {
                if (d.getHeureDebut() == null || d.getHeureFin() == null) continue;
                if (!d.getHeureFin().isAfter(d.getHeureDebut())) continue;
                if (!correspondJour(d.getJour(), dow)) continue;

                LocalDateTime curseur = jour.atTime(d.getHeureDebut());
                LocalDateTime limite  = jour.atTime(d.getHeureFin());

                while (!curseur.plusMinutes(dureeMin).isAfter(limite)) {
                    creneaux.add(new LocalDateTime[]{
                        curseur,
                        curseur.plusMinutes(dureeMin)
                    });
                    curseur = curseur.plusMinutes(dureeMin + PAUSE_MIN);
                }
            }
        }
        return creneaux;
    }

    private boolean correspondJour(Object jourEnum, DayOfWeek dow) {
        if (jourEnum == null) return false;
        String j = jourEnum.toString().toUpperCase();
        switch (dow) {
            case MONDAY:    return j.equals("LUNDI");
            case TUESDAY:   return j.equals("MARDI");
            case WEDNESDAY: return j.equals("MERCREDI");
            case THURSDAY:  return j.equals("JEUDI");
            case FRIDAY:    return j.equals("VENDREDI");
            case SATURDAY:  return j.equals("SAMEDI");
            case SUNDAY:    return j.equals("DIMANCHE");
            default:        return false;
        }
    }
}