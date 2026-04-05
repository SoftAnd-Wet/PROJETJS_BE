package net.codejava.projetjs_be.dto;

import lombok.*;
import net.codejava.projetjs_be.enums.StatutInvitation;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class InvitationDTO {
    private Long id;
    private Long groupeId;
    private String groupeNom;
    private Long emetteurId;
    private String emetteurNom;
    private Long destinataireId;
    private String destinataireNom;
    private StatutInvitation statut;
    private LocalDateTime creeLe;
}