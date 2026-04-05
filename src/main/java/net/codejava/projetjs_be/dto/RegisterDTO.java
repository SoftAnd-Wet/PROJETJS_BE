package net.codejava.projetjs_be.dto;


import jakarta.validation.constraints.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class RegisterDTO {
    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank @Email(message = "Email invalide")
    private String email;

    @NotBlank @Size(min = 6, message = "Mot de passe min 6 caractères")
    private String motDePasse;
}
