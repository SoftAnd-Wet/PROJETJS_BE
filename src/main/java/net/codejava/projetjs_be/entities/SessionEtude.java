package net.codejava.projetjs_be.entities;

import jakarta.persistence.*;
import lombok.*;
import net.codejava.projetjs_be.enums.StatutSession;


import java.time.LocalDateTime;

@Entity
@Table(name = "sessions_etude")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionEtude {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ SOLUTION : champ nom libre pour les sessions de groupe
    // Persiste le nom saisi par l'utilisateur directement en BDD
    // Survit à la déconnexion sans dépendre du localStorage
    @Column(name = "nom", length = 255)
    private String nom;

    @Column(name = "debut")
    private LocalDateTime debut;

    @Column(name = "fin")
    private LocalDateTime fin;

    @Column(name = "duree_prevue_min")
    private int dureePrevueMin;

    @Column(name = "duree_reelle_min")
    private int dureeReelleMin;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut")
    private StatutSession statut;

    @Column(name = "completee")
    private boolean completee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matiere_id")
    private Matiere matiere;
}