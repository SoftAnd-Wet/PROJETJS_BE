// dto/MatiereDTO.java
package net.codejava.projetjs_be.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class MatiereDTO {
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @Min(1) @Max(3)
    private int priorite;

    private int objectifHebdoHeures;
    private float progression;
}