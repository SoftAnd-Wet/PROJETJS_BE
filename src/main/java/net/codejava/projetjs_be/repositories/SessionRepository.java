package net.codejava.projetjs_be.repositories;

import net.codejava.projetjs_be.entities.SessionEtude;
import net.codejava.projetjs_be.enums.StatutSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<SessionEtude, Long> {

    List<SessionEtude> findByUtilisateurId(Long userId);

    List<SessionEtude> findByUtilisateurIdAndStatut(Long userId, StatutSession statut);

    List<SessionEtude> findByUtilisateurIdAndDebutBetween(
            Long userId, LocalDateTime debut, LocalDateTime fin);

    @Query("SELECT SUM(s.dureeReelleMin) FROM SessionEtude s " +
           "WHERE s.utilisateur.id = :userId AND s.completee = true")
    Long sumDureeReelleByUserId(@Param("userId") Long userId);

    @Query("SELECT s FROM SessionEtude s " +
           "JOIN SessionPartagee sp ON sp.session.id = s.id " +
           "WHERE sp.groupe.id = :groupeId")
    List<SessionEtude> findByGroupeId(@Param("groupeId") Long groupeId);

    @Modifying
    @Transactional
    @Query("DELETE FROM SessionEtude s WHERE s.utilisateur.id = :userId " +
           "AND s.debut BETWEEN :debut AND :fin AND s.statut = :statut")
    void deleteByUtilisateurIdAndDebutBetweenAndStatut(
            @Param("userId") Long userId,
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin,
            @Param("statut") StatutSession statut);
}