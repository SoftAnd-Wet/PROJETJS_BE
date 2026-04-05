// dto/CommentaireDTO.java
package net.codejava.projetjs_be.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CommentaireDTO {
    private Long id;

    @NotBlank(message = "Le contenu est obligatoire")
    private String contenu;

    private LocalDateTime creeLe;
    private Long sessionId;
    private Long auteurId;
    private String auteurNom;
}