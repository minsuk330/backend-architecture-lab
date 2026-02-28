package com.backend.lab.domain.notification.repository;

import com.backend.lab.domain.notification.entity.Notification;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

  @Query("SELECT n FROM Notification n WHERE n.memberId = ?1 AND n.createdAt BETWEEN ?2 AND ?3")
  List<Notification> findAllByMemberId(Long memberId, LocalDateTime start, LocalDateTime end);
}
