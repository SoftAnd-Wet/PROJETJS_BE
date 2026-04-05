package net.codejava.projetjs_be.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;


import java.util.List;
import net.codejava.projetjs_be.enums.StatutSession;

@Entity
@Table(name = "sessions_etude")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class SessionEtude {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime debut;

    @Column(nullable = false)
    private LocalDateTime fin;

    private int dureePrevueMin;
    private int dureeReelleMin;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private StatutSession statut = StatutSession.PLANIFIEE;

    @Builder.Default
    private boolean completee = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matiere_id")
    private Matiere matiere;
    // Ajouter ces deux champs dans SessionEtude.java

    @Builder.Default
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Commentaire> commentaires = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SessionPartagee> sessionsPartagees = new ArrayList<>();
}