package net.codejava.projetjs_be.repositories;

import net.codejava.projetjs_be.entities.SessionPartagee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface SessionPartageeRepository extends JpaRepository<SessionPartagee, Long> {

    // Toutes les sessions partagées dans un groupe
    List<SessionPartagee> findByGroupeId(Long groupeId);

    // Toutes les sessions partagées d'une session donnée
    List<SessionPartagee> findBySessionId(Long sessionId);

    // Vérifier si une session est déjà partagée dans un groupe
    boolean existsBySessionIdAndGroupeId(Long sessionId, Long groupeId);
    //  cette méthode pour la suppression en cascade
    @Modifying
    @Transactional
    @Query("DELETE FROM SessionPartagee sp WHERE sp.session.id IN :ids")
    void deleteBySessionIdIn(List<Long> ids);
    
    @Query("SELECT sp FROM SessionPartagee sp JOIN sp.groupe g JOIN g.membres m " +
           "WHERE m.utilisateur.id = :userId")
    List<SessionPartagee> findByUtilisateurId(@Param("userId") Long userId);
}