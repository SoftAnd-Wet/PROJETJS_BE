package net.codejava.projetjs_be.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class SessionPartageeDTO {
    private Long id;
    private Long sessionId;
    private String sessionMatiereNom;
    private LocalDateTime sessionDebut;
    private LocalDateTime sessionFin;
    private Long groupeId;
    private String groupeNom;
    private LocalDateTime partageLe;
}