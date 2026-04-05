package net.codejava.projetjs_be.dto;


import lombok.*;
import net.codejava.projetjs_be.enums.Role;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class UtilisateurDTO {
    private Long id;
    private String nom;
    private String email;
    private Role role;
    private LocalDateTime dateInscription;
}
