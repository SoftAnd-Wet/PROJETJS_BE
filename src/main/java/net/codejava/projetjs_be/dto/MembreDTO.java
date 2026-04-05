package net.codejava.projetjs_be.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembreDTO {
    private Long id;
    private Long utilisateurId;
    private String nom;
    private String email;
    private String role;
}