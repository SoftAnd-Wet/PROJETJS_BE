package net.codejava.projetjs_be.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CommentaireDTO {
    private Long          id;
    private String        contenu;
    private String        auteurNom;
    private Long          auteurId;
    private LocalDateTime creeLe;
}