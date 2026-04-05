package net.codejava.projetjs_be.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import net.codejava.projetjs_be.enums.TypeNotif;

@Entity
@Table(name = "notifications")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TypeNotif type;

    @Column(nullable = false)
    private String message;
    
    @Builder.Default
    private boolean lue = false;
    
    @Builder.Default
    private LocalDateTime envoyeeLe = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur destinataire;
}
