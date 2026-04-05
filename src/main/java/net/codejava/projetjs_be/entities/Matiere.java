package net.codejava.projetjs_be.entities;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "matieres")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Matiere {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private int priorite; // 1 = haute, 2 = moyenne, 3 = basse

    private int objectifHebdoHeures;
    
    @Builder.Default
    private float progression = 0f;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @Builder.Default
    @OneToMany(mappedBy = "matiere", cascade = CascadeType.ALL)
    private List<SessionEtude> sessions = new ArrayList<>();
}