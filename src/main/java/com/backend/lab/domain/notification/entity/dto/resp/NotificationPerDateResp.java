package com.backend.lab.domain.notification.entity.dto.resp;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPerDateResp {
  private Map<LocalDate, List<NotificationResp>> data;
}
