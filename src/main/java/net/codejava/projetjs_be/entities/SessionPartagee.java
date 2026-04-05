package net.codejava.projetjs_be.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sessions_partagees")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class SessionPartagee {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private SessionEtude session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupe_id", nullable = false)
    private GroupeEtude groupe;

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime partageLe = LocalDateTime.now();
}