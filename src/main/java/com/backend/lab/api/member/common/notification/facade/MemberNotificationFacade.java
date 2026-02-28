package com.backend.lab.api.member.common.notification.facade;

import com.backend.lab.domain.notification.entity.Notification;
import com.backend.lab.domain.notification.entity.dto.resp.NotificationPerDateResp;
import com.backend.lab.domain.notification.service.NotificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberNotificationFacade {

  private final NotificationService notificationService;

  public NotificationPerDateResp gets(Long userId) {
    List<Notification> notifications = notificationService.getsByMemberId(userId);
    return notificationService.notificationPerDateResp(notifications);
  }

  @Transactional
  public void read(Long notificationId) {
    notificationService.read(notificationId);
  }

  @Transactional
  public void readAll(Long userId) {
    notificationService.getsByMemberId(userId)
        .forEach(notification -> notificationService.read(notification.getId()));
  }
}
