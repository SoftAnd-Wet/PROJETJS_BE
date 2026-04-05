// dto/ChangeMotDePasseDTO.java
package net.codejava.projetjs_be.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ChangeMotDePasseDTO {
    @NotBlank(message = "Ancien mot de passe obligatoire")
    private String ancienMotDePasse;

    @NotBlank @Size(min = 6, message = "Nouveau mot de passe min 6 caractères")
    private String nouveauMotDePasse;
}
