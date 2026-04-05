package net.codejava.projetjs_be.entities;

import jakarta.persistence.*;
import lombok.*;
import net.codejava.projetjs_be.enums.StatutInvitation;
import java.time.LocalDateTime;

@Entity
@Table(name = "invitations")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Invitation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupe_id", nullable = false)
    private GroupeEtude groupe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emetteur_id", nullable = false)
    private Utilisateur emetteur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinataire_id", nullable = false)
    private Utilisateur destinataire;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutInvitation statut = StatutInvitation.EN_ATTENTE;

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime creeLe = LocalDateTime.now();
}