package net.codejava.projetjs_be.repositories;

import net.codejava.projetjs_be.entities.Membre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface MembreRepository extends JpaRepository<Membre, Long> {
    Optional<Membre> findByUtilisateurIdAndGroupeId(Long userId, Long groupeId);
    boolean existsByUtilisateurIdAndGroupeId(Long userId, Long groupeId);
    List<Membre> findByGroupeId(Long groupeId);
    List<Membre> findByUtilisateurId(Long userId); 
}