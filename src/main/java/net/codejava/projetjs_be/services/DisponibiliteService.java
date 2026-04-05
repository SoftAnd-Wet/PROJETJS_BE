package net.codejava.projetjs_be.services;

import lombok.RequiredArgsConstructor;
import net.codejava.projetjs_be.dto.DisponibiliteDTO;
import net.codejava.projetjs_be.entities.*;
import net.codejava.projetjs_be.repositories.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DisponibiliteService {

    private final DisponibiliteRepository disponibiliteRepository;
    private final UtilisateurRepository utilisateurRepository;

    public List<DisponibiliteDTO> getMesDisponibilites(Long userId) {
        return disponibiliteRepository.findByUtilisateurId(userId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public DisponibiliteDTO creer(Long userId, DisponibiliteDTO dto) {
        Utilisateur u = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        Disponibilite d = Disponibilite.builder()
                .jour(dto.getJour())
                .heureDebut(dto.getHeureDebut())
                .heureFin(dto.getHeureFin())
                .utilisateur(u)
                .build();
        return toDTO(disponibiliteRepository.save(d));
    }

    public void supprimer(Long userId, Long id) {
        Disponibilite d = disponibiliteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Disponibilité non trouvée"));
        if (!d.getUtilisateur().getId().equals(userId))
            throw new RuntimeException("Accès refusé");
        disponibiliteRepository.delete(d);
    }

    private DisponibiliteDTO toDTO(Disponibilite d) {
        return DisponibiliteDTO.builder()
                .id(d.getId()).jour(d.getJour())
                .heureDebut(d.getHeureDebut())
                .heureFin(d.getHeureFin())
                .build();
    }
}