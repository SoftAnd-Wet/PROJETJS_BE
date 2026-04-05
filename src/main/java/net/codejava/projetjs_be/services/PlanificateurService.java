package net.codejava.projetjs_be.services;

import lombok.RequiredArgsConstructor;
import net.codejava.projetjs_be.dto.SessionDTO;
import net.codejava.projetjs_be.entities.*;
import net.codejava.projetjs_be.enums.StatutSession;
import net.codejava.projetjs_be.repositories.*;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanificateurService {

    private final MatiereRepository matiereRepository;
    private final DisponibiliteRepository disponibiliteRepository;
    private final SessionRepository sessionRepository;
    private final UtilisateurRepository utilisateurRepository;

    private static final int DUREE_SESSION_MIN = 60;  // 1h par session
    private static final int PAUSE_MIN = 15;           // 15 min entre sessions

    public List<SessionDTO> genererPlanning(Long userId, LocalDate semaine) {
        // 1. Récupérer les matières triées par priorité
        List<Matiere> matieres = matiereRepository
                .findByUtilisateurIdOrderByPrioriteAsc(userId);

        // 2. Récupérer les disponibilités
        List<Disponibilite> dispos = disponibiliteRepository
                .findByUtilisateurId(userId);

        if (matieres.isEmpty() || dispos.isEmpty()) return List.of();

        // 3. Générer les créneaux disponibles pour la semaine
        List<LocalDateTime[]> creneaux = genererCreneaux(semaine, dispos);

        // 4. Supprimer anciennes sessions planifiées de la semaine
        LocalDateTime debutSemaine = semaine.atStartOfDay();
        LocalDateTime finSemaine = semaine.plusDays(7).atStartOfDay();
        sessionRepository.deleteByUtilisateurIdAndDebutBetweenAndStatut(
                userId, debutSemaine, finSemaine, StatutSession.PLANIFIEE);

        // 5. Affecter greedy : matière prioritaire → premier créneau libre
        Utilisateur u = utilisateurRepository.findById(userId).orElseThrow();
        List<SessionEtude> sessions = new ArrayList<>();
        int creneauIndex = 0;

        for (Matiere m : matieres) {
            int heuresRestantes = m.getObjectifHebdoHeures();
            while (heuresRestantes > 0 && creneauIndex < creneaux.size()) {
                LocalDateTime[] creneau = creneaux.get(creneauIndex);
                SessionEtude s = SessionEtude.builder()
                        .debut(creneau[0])
                        .fin(creneau[0].plusMinutes(DUREE_SESSION_MIN))
                        .dureePrevueMin(DUREE_SESSION_MIN)
                        .statut(StatutSession.PLANIFIEE)
                        .completee(false)
                        .utilisateur(u)
                        .matiere(m)
                        .build();
                sessions.add(s);
                heuresRestantes--;
                creneauIndex++;
            }
        }

        List<SessionEtude> saved = sessionRepository.saveAll(sessions);
        return saved.stream().map(s -> SessionDTO.builder()
                .id(s.getId())
                .debut(s.getDebut()).fin(s.getFin())
                .dureePrevueMin(s.getDureePrevueMin())
                .statut(s.getStatut())
                .matiereId(s.getMatiere().getId())
                .matiereNom(s.getMatiere().getNom())
                .build()).collect(Collectors.toList());
    }

    private List<LocalDateTime[]> genererCreneaux(LocalDate semaine,
                                                   List<Disponibilite> dispos) {
        List<LocalDateTime[]> creneaux = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate jour = semaine.plusDays(i);
            DayOfWeek dow = jour.getDayOfWeek();
            for (Disponibilite d : dispos) {
                if (d.getJour().name().equals(dow.name())) {
                    LocalDateTime debut = jour.atTime(d.getHeureDebut());
                    LocalDateTime fin = jour.atTime(d.getHeureFin());
                    LocalDateTime curseur = debut;
                    while (!curseur.plusMinutes(DUREE_SESSION_MIN).isAfter(fin)) {
                        creneaux.add(new LocalDateTime[]{curseur,
                                curseur.plusMinutes(DUREE_SESSION_MIN)});
                        curseur = curseur.plusMinutes(DUREE_SESSION_MIN + PAUSE_MIN);
                    }
                }
            }
        }
        return creneaux;
    }
}