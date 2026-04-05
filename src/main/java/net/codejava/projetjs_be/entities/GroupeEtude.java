package net.codejava.projetjs_be.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "groupes_etude")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class GroupeEtude {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    private String description;
    
    @Builder.Default
    private LocalDate dateCreation = LocalDate.now();

    @Builder.Default
    @OneToMany(mappedBy = "groupe", cascade = CascadeType.ALL)
    private List<Membre> membres = new ArrayList<>();
    // Ajouter ce champ dans GroupeEtude.java
   
     @Builder.Default
    @OneToMany(mappedBy = "groupe", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SessionPartagee> sessionsPartagees = new ArrayList<>();
}