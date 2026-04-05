package net.codejava.projetjs_be.repositories;

import net.codejava.projetjs_be.entities.SessionPartagee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SessionPartageeRepository extends JpaRepository<SessionPartagee, Long> {

    // Toutes les sessions partagées dans un groupe
    List<SessionPartagee> findByGroupeId(Long groupeId);

    // Toutes les sessions partagées d'une session donnée
    List<SessionPartagee> findBySessionId(Long sessionId);

    // Vérifier si une session est déjà partagée dans un groupe
    boolean existsBySessionIdAndGroupeId(Long sessionId, Long groupeId);
}