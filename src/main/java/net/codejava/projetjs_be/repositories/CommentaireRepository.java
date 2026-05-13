package net.codejava.projetjs_be.repositories;

import net.codejava.projetjs_be.entities.Commentaire;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentaireRepository extends JpaRepository<Commentaire, Long> {
    List<Commentaire> findBySessionIdOrderByCreeLe(Long sessionId);
}