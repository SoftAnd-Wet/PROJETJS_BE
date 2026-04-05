package net.codejava.projetjs_be.services;

import lombok.RequiredArgsConstructor;
import net.codejava.projetjs_be.dto.InvitationDTO;
import net.codejava.projetjs_be.entities.*;
import net.codejava.projetjs_be.enums.*;
import net.codejava.projetjs_be.repositories.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvitationService {

    private final InvitationRepository  invitationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final GroupeRepository      groupeRepository;
    private final MembreRepository      membreRepository;

    // Envoyer une invitation
    public InvitationDTO envoyerInvitation(Long emetteurId,
                                           Long groupeId,
                                           String emailDestinataire) {
        Utilisateur emetteur = utilisateurRepository.findById(emetteurId)
                .orElseThrow(() -> new RuntimeException("Émetteur non trouvé"));

        GroupeEtude groupe = groupeRepository.findById(groupeId)
                .orElseThrow(() -> new RuntimeException("Groupe non trouvé"));

        // Vérifier que l'émetteur est membre du groupe
        if (!membreRepository.existsByUtilisateurIdAndGroupeId(emetteurId, groupeId)) {
            throw new RuntimeException("Vous n'êtes pas membre de ce groupe");
        }

        Utilisateur destinataire = utilisateurRepository
                .findByEmail(emailDestinataire)
                .orElseThrow(() -> new RuntimeException(
                        "Aucun utilisateur trouvé avec l'email : " + emailDestinataire));

        // Vérifier que le destinataire n'est pas déjà membre
        if (membreRepository.existsByUtilisateurIdAndGroupeId(
                destinataire.getId(), groupeId)) {
            throw new RuntimeException("Cet utilisateur est déjà membre du groupe");
        }

        // Vérifier qu'une invitation en attente n'existe pas déjà
        invitationRepository
                .findByGroupeIdAndDestinataireId(groupeId, destinataire.getId())
                .ifPresent(inv -> {
                    if (inv.getStatut() == StatutInvitation.EN_ATTENTE) {
                        throw new RuntimeException(
                                "Une invitation est déjà en attente pour cet utilisateur");
                    }
                });

        Invitation invitation = Invitation.builder()
                .groupe(groupe)
                .emetteur(emetteur)
                .destinataire(destinataire)
                .statut(StatutInvitation.EN_ATTENTE)
                .build();

        return toDTO(invitationRepository.save(invitation));
    }

    // Récupérer les invitations reçues
    public List<InvitationDTO> getMesInvitations(Long userId) {
        return invitationRepository
                .findByDestinataireId(userId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Récupérer uniquement les invitations en attente
    public List<InvitationDTO> getMesInvitationsEnAttente(Long userId) {
        return invitationRepository
                .findByDestinataireIdAndStatut(userId, StatutInvitation.EN_ATTENTE)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Accepter une invitation
    public void accepterInvitation(Long userId, Long invitationId) {
        Invitation inv = getInvitationVerifiee(userId, invitationId);

        inv.setStatut(StatutInvitation.ACCEPTEE);
        invitationRepository.save(inv);

        // Ajouter automatiquement au groupe
        if (!membreRepository.existsByUtilisateurIdAndGroupeId(
                userId, inv.getGroupe().getId())) {
            Membre membre = Membre.builder()
                    .utilisateur(inv.getDestinataire())
                    .groupe(inv.getGroupe())
                    .role(RoleMembre.MEMBRE)
                    .build();
            membreRepository.save(membre);
        }
    }

    // Refuser une invitation
    public void refuserInvitation(Long userId, Long invitationId) {
        Invitation inv = getInvitationVerifiee(userId, invitationId);
        inv.setStatut(StatutInvitation.REFUSEE);
        invitationRepository.save(inv);
    }

    private Invitation getInvitationVerifiee(Long userId, Long invitationId) {
        Invitation inv = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new RuntimeException("Invitation non trouvée"));

        if (!inv.getDestinataire().getId().equals(userId)) {
            throw new RuntimeException("Accès refusé");
        }
        if (inv.getStatut() != StatutInvitation.EN_ATTENTE) {
            throw new RuntimeException("Invitation déjà traitée");
        }
        return inv;
    }

    private InvitationDTO toDTO(Invitation inv) {
        return InvitationDTO.builder()
                .id(inv.getId())
                .groupeId(inv.getGroupe().getId())
                .groupeNom(inv.getGroupe().getNom())
                .emetteurId(inv.getEmetteur().getId())
                .emetteurNom(inv.getEmetteur().getNom())
                .destinataireId(inv.getDestinataire().getId())
                .destinataireNom(inv.getDestinataire().getNom())
                .statut(inv.getStatut())
                .creeLe(inv.getCreeLe())
                .build();
    }
}