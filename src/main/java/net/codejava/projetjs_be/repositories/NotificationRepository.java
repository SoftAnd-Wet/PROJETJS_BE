package net.codejava.projetjs_be.repositories;

import net.codejava.projetjs_be.entities.Notification;
import net.codejava.projetjs_be.enums.TypeNotif;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByDestinataireIdOrderByEnvoyeeLeDesc(Long userId);

    List<Notification> findByDestinataireIdAndLueFalseOrderByEnvoyeeLeDesc(Long userId);

    List<Notification> findByDestinataireIdAndTypeOrderByEnvoyeeLeDesc(
            Long userId, TypeNotif type);

    long countByDestinataireIdAndLueFalse(Long userId);
}