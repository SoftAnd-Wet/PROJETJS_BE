package net.codejava.projetjs_be.repositories;

import net.codejava.projetjs_be.entities.Invitation;
import net.codejava.projetjs_be.enums.StatutInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    // Toutes les invitations reçues par un utilisateur
    List<Invitation> findByDestinataireId(Long destinataireId);

    // Invitations en attente reçues
    List<Invitation> findByDestinataireIdAndStatut(
            Long destinataireId, StatutInvitation statut);

    // Vérifier si une invitation existe déjà
    Optional<Invitation> findByGroupeIdAndDestinataireId(
            Long groupeId, Long destinataireId);

    // Invitations envoyées par un utilisateur
    List<Invitation> findByEmetteurId(Long emetteurId);
}