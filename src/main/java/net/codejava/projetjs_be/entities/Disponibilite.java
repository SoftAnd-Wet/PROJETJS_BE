package net.codejava.projetjs_be.entities;
import jakarta.persistence.*;
import net.codejava.projetjs_be.enums.JourSemaine;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "disponibilites")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Disponibilite {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JourSemaine jour;

    @Column(nullable = false)
    private LocalTime heureDebut;

    @Column(nullable = false)
    private LocalTime heureFin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;
}