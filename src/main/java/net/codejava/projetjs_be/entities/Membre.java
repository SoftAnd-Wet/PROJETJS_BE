package net.codejava.projetjs_be.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import net.codejava.projetjs_be.enums.RoleMembre;

@Entity
@Table(name = "membres")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Membre {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupe_id")
    private GroupeEtude groupe;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private RoleMembre role = RoleMembre.MEMBRE;

    @Builder.Default
    private LocalDate dateAdhesion = LocalDate.now();
}