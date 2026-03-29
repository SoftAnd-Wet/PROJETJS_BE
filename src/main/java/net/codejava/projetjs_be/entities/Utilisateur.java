package net.codejava.projetjs_be.entities;

import net.codejava.projetjs_be.enums.Role;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class Utilisateur {
    @Id
    private Long id;
    private String nom;
    private String email;
    private String motDePasse;
    private Role role;
    LocalDateTime dateInscription;
}
