package net.codejava.projetjs_be.repositories;


import net.codejava.projetjs_be.entities.Matiere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface MatiereRepository extends JpaRepository<Matiere, Long> {
    List<Matiere> findByUtilisateurIdOrderByPrioriteAsc(Long userId);
}
