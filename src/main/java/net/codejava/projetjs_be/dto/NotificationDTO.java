package net.codejava.projetjs_be.dto;

import lombok.*;
import net.codejava.projetjs_be.enums.TypeNotif;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class NotificationDTO {
    private Long id;
    private TypeNotif type;
    private String message;
    private boolean lue;
    private LocalDateTime envoyeeLe;
    private Long destinataireId;
}