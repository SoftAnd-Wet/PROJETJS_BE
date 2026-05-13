package net.codejava.projetjs_be.services;

import lombok.RequiredArgsConstructor;
import net.codejava.projetjs_be.dto.CommentaireDTO;
import net.codejava.projetjs_be.entities.*;
import net.codejava.projetjs_be.repositories.*;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentaireService {

    private final CommentaireRepository  commentaireRepository;
    private final SessionRepository      sessionRepository;
    private final UtilisateurRepository  utilisateurRepository;

    public List<CommentaireDTO> getCommentaires(Long userId, Long sessionId) {
        return commentaireRepository.findBySessionIdOrderByCreeLe(sessionId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public CommentaireDTO ajouter(Long userId, Long sessionId, String contenu) {
        SessionEtude s = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session non trouvée"));
        Utilisateur u = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Commentaire c = Commentaire.builder()
                .session(s)
                .auteur(u)
                .contenu(contenu)
                .creeLe(LocalDateTime.now())
                .build();

        return toDTO(commentaireRepository.save(c));
    }

    public void supprimer(Long userId, Long commentaireId) {
        Commentaire c = commentaireRepository.findById(commentaireId)
                .orElseThrow(() -> new RuntimeException("Commentaire non trouvé"));
        if (!c.getAuteur().getId().equals(userId))
            throw new RuntimeException("Accès refusé");
        commentaireRepository.delete(c);
    }

    private CommentaireDTO toDTO(Commentaire c) {
        return CommentaireDTO.builder()
                .id(c.getId())
                .contenu(c.getContenu())
                .auteurNom(c.getAuteur().getNom())
                .auteurId(c.getAuteur().getId())
                .creeLe(c.getCreeLe())
                .build();
    }
}