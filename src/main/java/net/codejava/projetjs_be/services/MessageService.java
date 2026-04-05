package net.codejava.projetjs_be.services;

import lombok.RequiredArgsConstructor;
import net.codejava.projetjs_be.dto.MessageDTO;
import net.codejava.projetjs_be.entities.*;
import net.codejava.projetjs_be.enums.TypeNotif;
import net.codejava.projetjs_be.repositories.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository      messageRepository;
    private final GroupeRepository       groupeRepository;
    private final UtilisateurRepository  utilisateurRepository;
    private final MembreRepository       membreRepository;
    private final NotificationRepository notificationRepository;

    public MessageDTO envoyerMessage(Long userId, Long groupeId, String contenu) {

        if (!membreRepository.existsByUtilisateurIdAndGroupeId(userId, groupeId)) {
            throw new RuntimeException("Vous n'êtes pas membre de ce groupe");
        }
        if (contenu == null || contenu.trim().isEmpty()) {
            throw new RuntimeException("Le message ne peut pas être vide");
        }

        Utilisateur auteur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        GroupeEtude groupe = groupeRepository.findById(groupeId)
                .orElseThrow(() -> new RuntimeException("Groupe non trouvé"));

        Message message = Message.builder()
                .contenu(contenu.trim())
                .auteur(auteur)
                .groupe(groupe)
                .build();

        Message saved = messageRepository.save(message);

        // Notifier tous les membres du groupe sauf l'auteur
        String apercu = contenu.length() > 50
                ? contenu.substring(0, 50) + "..."
                : contenu;

        List<Membre> membres = membreRepository.findByGroupeId(groupeId);
        for (Membre m : membres) {
            if (m.getUtilisateur().getId().equals(userId)) continue;

            Notification notif = Notification.builder()
                    .type(TypeNotif.MESSAGE_GROUPE)
                    .message(auteur.getNom() + " a écrit dans \""
                             + groupe.getNom() + "\" : " + apercu)
                    .lue(false)
                    .destinataire(m.getUtilisateur())
                    .build();
            notificationRepository.save(notif);
        }

        return toDTO(saved);
    }

    public List<MessageDTO> getMessages(Long userId, Long groupeId) {
        if (!membreRepository.existsByUtilisateurIdAndGroupeId(userId, groupeId)) {
            throw new RuntimeException("Vous n'êtes pas membre de ce groupe");
        }
        return messageRepository
                .findByGroupeIdOrderByEnvoyeLeAsc(groupeId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public void supprimerMessage(Long userId, Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message non trouvé"));
        if (!message.getAuteur().getId().equals(userId)) {
            throw new RuntimeException("Vous ne pouvez supprimer que vos propres messages");
        }
        messageRepository.delete(message);
    }

    public MessageDTO toDTO(Message m) {
        return MessageDTO.builder()
                .id(m.getId())
                .contenu(m.getContenu())
                .envoyeLe(m.getEnvoyeLe())
                .auteurId(m.getAuteur().getId())
                .auteurNom(m.getAuteur().getNom())
                .groupeId(m.getGroupe().getId())
                .build();
    }
}