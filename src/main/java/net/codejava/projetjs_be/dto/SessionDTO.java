package net.codejava.projetjs_be.dto;

import lombok.*;
import net.codejava.projetjs_be.enums.StatutSession;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class SessionDTO {
    private Long id;
    private LocalDateTime debut;
    private LocalDateTime fin;
    private int dureePrevueMin;
    private int dureeReelleMin;
    private StatutSession statut;
    private boolean completee;
    private Long matiereId;
    private String matiereNom;
}