package net.codejava.projetjs_be.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class TokenDTO {
    private String accessToken;
    @Builder.Default
    private String tokenType = "Bearer";
    private Long userId;
    private String nom;
    private String role;
}