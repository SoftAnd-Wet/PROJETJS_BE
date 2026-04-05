package net.codejava.projetjs_be.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import net.codejava.projetjs_be.dto.ApiResponse;
import net.codejava.projetjs_be.entities.Utilisateur;
import net.codejava.projetjs_be.services.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboard(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(
                dashboardService.getDashboard(getUserId(auth))));
    }

    private Long getUserId(Authentication auth) {
        return ((Utilisateur) auth.getPrincipal()).getId();
    }
}