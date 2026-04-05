package net.codejava.projetjs_be.repositories;

import net.codejava.projetjs_be.entities.Disponibilite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DisponibiliteRepository extends JpaRepository<Disponibilite, Long> {
    List<Disponibilite> findByUtilisateurId(Long userId);
}