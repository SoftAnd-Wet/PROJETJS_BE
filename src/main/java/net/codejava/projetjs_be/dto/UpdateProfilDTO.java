// dto/UpdateProfilDTO.java
package net.codejava.projetjs_be.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class UpdateProfilDTO {
    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank @Email(message = "Email invalide")
    private String email;
}