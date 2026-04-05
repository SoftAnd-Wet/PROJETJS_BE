package net.codejava.projetjs_be.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class MessageDTO {
    private Long id;
    private String contenu;
    private LocalDateTime envoyeLe;
    private Long auteurId;
    private String auteurNom;
    private Long groupeId;
}