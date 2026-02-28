package com.backend.lab.domain.notification.entity;

import com.backend.lab.common.entity.BaseEntity;
import com.backend.lab.domain.notification.entity.vo.NotificationType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "notification")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Notification extends BaseEntity {

  private Long memberId;

  private String title;
  private String content;
  @Enumerated(EnumType.STRING)
  private NotificationType type;

  private Long payload;

  private boolean isRead;
}
