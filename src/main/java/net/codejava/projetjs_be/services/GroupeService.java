package net.codejava.projetjs_be.services;

import lombok.RequiredArgsConstructor;
import net.codejava.projetjs_be.dto.GroupeDTO;
import net.codejava.projetjs_be.dto.MembreDTO;
import net.codejava.projetjs_be.dto.SessionDTO;
import net.codejava.projetjs_be.entities.*;
import net.codejava.projetjs_be.enums.RoleMembre;
import net.codejava.projetjs_be.repositories.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupeService {

    private final GroupeRepository groupeRepository;
    private final MembreRepository membreRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final SessionRepository sessionRepository;

    public List<GroupeDTO> getMesGroupes(Long userId) {
        return groupeRepository.findByMembreUserId(userId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public GroupeDTO creer(Long userId, GroupeDTO dto) {
        Utilisateur u = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        GroupeEtude g = GroupeEtude.builder()
                .nom(dto.getNom())
                .description(dto.getDescription())
                .build();
        groupeRepository.save(g);
        Membre proprietaire = Membre.builder()
                .utilisateur(u).groupe(g)
                .role(RoleMembre.PROPRIETAIRE)
                .build();
        membreRepository.save(proprietaire);
        return toDTO(g);
    }

   public void inviterMembre(Long userId, Long groupeId, String email) {
        GroupeEtude g = groupeRepository.findById(groupeId)
                .orElseThrow(() -> new RuntimeException("Groupe non trouvé"));
        verifierProprietaire(userId, groupeId);
        Utilisateur invite = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        if (membreRepository.existsByUtilisateurIdAndGroupeId(invite.getId(), groupeId))
            throw new RuntimeException("Déjà membre du groupe");
        Membre m = Membre.builder()
                  .utilisateur(invite).groupe(g)
                  .role(RoleMembre.MEMBRE)
                  .build();
        membreRepository.save(m);
    }

    public void quitterGroupe(Long userId, Long groupeId) {
        Membre m = membreRepository.findByUtilisateurIdAndGroupeId(userId, groupeId)
                .orElseThrow(() -> new RuntimeException("Membre non trouvé"));
        membreRepository.delete(m);
    }

    public List<SessionDTO> getSessionsGroupe(Long groupeId) {
        return sessionRepository.findByGroupeId(groupeId)
                .stream().map(s -> SessionDTO.builder()
                        .id(s.getId())
                        .debut(s.getDebut()).fin(s.getFin())
                        .statut(s.getStatut())
                        .matiereNom(s.getMatiere() != null ? s.getMatiere().getNom() : null)
                        .build())
                .collect(Collectors.toList());
    }

    private void verifierProprietaire(Long userId, Long groupeId) {
        membreRepository.findByUtilisateurIdAndGroupeId(userId, groupeId)
                .filter(m -> m.getRole() == RoleMembre.PROPRIETAIRE)
                .orElseThrow(() -> new RuntimeException("Seul le propriétaire peut effectuer cette action"));
    }

    private GroupeDTO toDTO(GroupeEtude g) {
        return GroupeDTO.builder()
                .id(g.getId()).nom(g.getNom())
                .description(g.getDescription())
                .build();
    }
    public List<MembreDTO> getMembresGroupe(Long userId, Long groupeId) {
        if (!membreRepository.existsByUtilisateurIdAndGroupeId(userId, groupeId)) {
            throw new RuntimeException("Vous n'êtes pas membre de ce groupe");
        }

        return membreRepository.findByGroupeId(groupeId)
                .stream()
                .map(m -> MembreDTO.builder()
                        .id(m.getId())
                        .utilisateurId(m.getUtilisateur().getId())
                        .nom(m.getUtilisateur().getNom())
                        .email(m.getUtilisateur().getEmail())
                        .role(m.getRole().name())
                        .build())
                .collect(Collectors.toList());
    }
}
