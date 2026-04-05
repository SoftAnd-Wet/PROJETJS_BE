package net.codejava.projetjs_be.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class GroupeDTO {
    private Long id;

    @NotBlank
    private String nom;

    private String description;
}