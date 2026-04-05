package net.codejava.projetjs_be.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class LoginDTO {
    @NotBlank @Email
    private String email;

    @NotBlank
    private String motDePasse;
}
