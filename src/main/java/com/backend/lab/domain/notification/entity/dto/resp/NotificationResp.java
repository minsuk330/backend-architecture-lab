package com.backend.lab.domain.notification.entity.dto.resp;

import com.backend.lab.common.entity.dto.resp.BaseResp;
import com.backend.lab.domain.notification.entity.vo.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResp extends BaseResp {

  private String title;
  private String content;
  private NotificationType type;
  private Long payload;
  private boolean isRead;
}
