package net.codejava.projetjs_be.repositories;

import net.codejava.projetjs_be.entities.Commentaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentaireRepository extends JpaRepository<Commentaire, Long> {

    // Tous les commentaires d'une session triés par date
    List<Commentaire> findBySessionIdOrderByCreeLe(Long sessionId);

    // Tous les commentaires d'un utilisateur
    List<Commentaire> findByAuteurId(Long auteurId);
}
