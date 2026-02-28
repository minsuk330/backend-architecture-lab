package com.backend.lab.domain.notification.service;

import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import com.backend.lab.domain.notification.entity.Notification;
import com.backend.lab.domain.notification.entity.dto.resp.NotificationPerDateResp;
import com.backend.lab.domain.notification.entity.dto.resp.NotificationResp;
import com.backend.lab.domain.notification.entity.vo.NotificationType;
import com.backend.lab.domain.notification.repository.NotificationRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

  private final NotificationRepository notificationRepository;

  public Notification getById(Long id) {
    return notificationRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Notification"));
  }

  public List<Notification> getsByMemberId(Long memberId) {
    LocalDate start = LocalDate.now().minusWeeks(1);
    LocalDate end = LocalDate.now().plusDays(1);
    return notificationRepository.findAllByMemberId(memberId, start.atStartOfDay(), end.atStartOfDay());
  }

  @Transactional
  public void createByProperty(Long memberId, Long payload, String buildingName, NotificationType type ) {

    String title;
    String content;

    switch (type) {
      case REGISTER -> {
        title = buildingName;
        content = "새로운 매물이 등록되었어요.";
      }
      default -> {
        throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
      }
    }

    notificationRepository.save(
        Notification.builder()
            .memberId(memberId)

            .title(title)
            .content(content)
            .type(type)

            .payload(payload)

            .isRead(false)
            .build()
    );
  }

  @Transactional
  public void read(Long id) {
    Notification notification = this.getById(id);
    notification.setRead(true);
  }

  public NotificationResp notificationResp(Notification notification) {
    return NotificationResp.builder()
        .id(notification.getId())
        .createdAt(notification.getCreatedAt())

        .title(notification.getTitle())
        .content(notification.getContent())
        .type(notification.getType())
        .payload(notification.getPayload())
        .isRead(notification.isRead())
        .build();
  }

  public NotificationPerDateResp notificationPerDateResp(List<Notification> notifications) {
    Map<LocalDate, List<NotificationResp>> data = notifications.stream()
        .map(this::notificationResp)
        .collect(Collectors.groupingBy(
            notification -> notification.getCreatedAt().toLocalDate(),
            Collectors.toList()
        ));

    return NotificationPerDateResp.builder()
        .data(data)
        .build();
  }
}
