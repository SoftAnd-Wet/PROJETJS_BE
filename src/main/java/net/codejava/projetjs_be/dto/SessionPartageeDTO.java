package net.codejava.projetjs_be.dto;

import lombok.*;
import java.time.LocalDateTime;

// ✅ FIX : pas d'import StatutSession — on utilise String pour le statut
// Évite la dépendance à l'enum et simplifie la sérialisation JSON
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class SessionPartageeDTO {
    private Long          id;                    // ID du partage
    private Long          sessionId;             // ✅ ID de la session (distinct de id)
    private String        sessionNom;            // ✅ nom libre persisté en BDD
    private String        sessionMatiereNom;     // nom matière si liée
    private LocalDateTime sessionDebut;          // ✅ date début
    private LocalDateTime sessionFin;            // ✅ date fin
    private int           sessionDureePrevueMin; // ✅ durée calculée
    private String        sessionStatut;         // ✅ String au lieu de StatutSession
    private Long          groupeId;
    private String        groupeNom;
    private LocalDateTime partageLe;
}