package net.codejava.projetjs_be.repositories;

import net.codejava.projetjs_be.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // Tous les messages d'un groupe triés par date
    List<Message> findByGroupeIdOrderByEnvoyeLeAsc(Long groupeId);

    // Les N derniers messages d'un groupe
    List<Message> findTop50ByGroupeIdOrderByEnvoyeLeDesc(Long groupeId);
}