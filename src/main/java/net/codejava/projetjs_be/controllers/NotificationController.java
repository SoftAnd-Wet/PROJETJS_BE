package net.codejava.projetjs_be.controllers;

import lombok.RequiredArgsConstructor;
import net.codejava.projetjs_be.dto.*;
import net.codejava.projetjs_be.entities.*;
import net.codejava.projetjs_be.repositories.NotificationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;

    private Long getUserId(Authentication auth) {
        return ((Utilisateur) auth.getPrincipal()).getId();
    }

    // GET /api/notifications
    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getMesNotifications(
            Authentication auth) {
        List<NotificationDTO> notifs = notificationRepository
                .findByDestinataireIdOrderByEnvoyeeLeDesc(getUserId(auth))
                .stream().map(this::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok(notifs));
    }

    // GET /api/notifications/non-lues
    @GetMapping("/non-lues")
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getNonLues(
            Authentication auth) {
        List<NotificationDTO> notifs = notificationRepository
                .findByDestinataireIdAndLueFalseOrderByEnvoyeeLeDesc(getUserId(auth))
                .stream().map(this::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok(notifs));
    }

    // GET /api/notifications/count
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getNombreNonLues(
            Authentication auth) {
        long count = notificationRepository
                .countByDestinataireIdAndLueFalse(getUserId(auth));
        return ResponseEntity.ok(ApiResponse.ok(count));
    }

    // PATCH /api/notifications/{id}/lire
    @PatchMapping("/{id}/lire")
    public ResponseEntity<ApiResponse<Void>> marquerLue(
            @PathVariable Long id,
            Authentication auth) {
        notificationRepository.findById(id).ifPresent(n -> {
            if (n.getDestinataire().getId().equals(getUserId(auth))) {
                n.setLue(true);
                notificationRepository.save(n);
            }
        });
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // PATCH /api/notifications/lire-toutes
    @PatchMapping("/lire-toutes")
    public ResponseEntity<ApiResponse<Void>> marquerToutesLues(
            Authentication auth) {
        List<Notification> notifs = notificationRepository
                .findByDestinataireIdAndLueFalseOrderByEnvoyeeLeDesc(getUserId(auth));
        notifs.forEach(n -> n.setLue(true));
        notificationRepository.saveAll(notifs);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    private NotificationDTO toDTO(Notification n) {
        return NotificationDTO.builder()
                .id(n.getId())
                .type(n.getType())
                .message(n.getMessage())
                .lue(n.isLue())
                .envoyeeLe(n.getEnvoyeeLe())
                .destinataireId(n.getDestinataire().getId())
                .build();
    }
}