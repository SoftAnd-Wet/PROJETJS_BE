package net.codejava.projetjs_be.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import net.codejava.projetjs_be.enums.JourSemaine;
import java.time.LocalTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DisponibiliteDTO {
    private Long id;

    @NotNull
    private JourSemaine jour;

    @NotNull
    private LocalTime heureDebut;

    @NotNull
    private LocalTime heureFin;
}