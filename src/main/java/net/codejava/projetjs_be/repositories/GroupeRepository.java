package net.codejava.projetjs_be.repositories;

import net.codejava.projetjs_be.entities.GroupeEtude;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

@Repository
public interface GroupeRepository extends JpaRepository<GroupeEtude, Long> {
    @Query("SELECT g FROM GroupeEtude g JOIN g.membres m WHERE m.utilisateur.id = :userId")
    List<GroupeEtude> findByMembreUserId(@Param("userId") Long userId);
}