// services/CommentaireService.java
package net.codejava.projetjs_be.services;

import lombok.RequiredArgsConstructor;
import net.codejava.projetjs_be.dto.CommentaireDTO;
import net.codejava.projetjs_be.entities.*;
import net.codejava.projetjs_be.repositories.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentaireService {

    private final CommentaireRepository commentaireRepository;
    private final SessionRepository sessionRepository;
    private final UtilisateurRepository utilisateurRepository;

    // Voir tous les commentaires d'une session
    public List<CommentaireDTO> getCommentaires(Long sessionId) {
        return commentaireRepository.findBySessionIdOrderByCreeLe(sessionId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Ajouter un commentaire
    public CommentaireDTO ajouter(Long userId, Long sessionId, CommentaireDTO dto) {
        Utilisateur auteur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        SessionEtude session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session non trouvée"));

        Commentaire c = Commentaire.builder()
                .contenu(dto.getContenu())
                .auteur(auteur)
                .session(session)
                .build();

        return toDTO(commentaireRepository.save(c));
    }

    // Modifier un commentaire
    public CommentaireDTO modifier(Long userId, Long id, CommentaireDTO dto) {
        Commentaire c = commentaireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commentaire non trouvé"));

        if (!c.getAuteur().getId().equals(userId)) {
            throw new RuntimeException("Vous ne pouvez modifier que vos propres commentaires");
        }

        c.setContenu(dto.getContenu());
        return toDTO(commentaireRepository.save(c));
    }

    // Supprimer un commentaire
    public void supprimer(Long userId, Long id) {
        Commentaire c = commentaireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commentaire non trouvé"));

        if (!c.getAuteur().getId().equals(userId)) {
            throw new RuntimeException("Vous ne pouvez supprimer que vos propres commentaires");
        }

        commentaireRepository.delete(c);
    }

    private CommentaireDTO toDTO(Commentaire c) {
        return CommentaireDTO.builder()
                .id(c.getId())
                .contenu(c.getContenu())
                .creeLe(c.getCreeLe())
                .sessionId(c.getSession().getId())
                .auteurId(c.getAuteur().getId())
                .auteurNom(c.getAuteur().getNom())
                .build();
    }
}