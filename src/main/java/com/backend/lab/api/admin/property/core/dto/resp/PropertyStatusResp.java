package com.backend.lab.api.admin.property.core.dto.resp;

import com.backend.lab.domain.property.core.entity.vo.PropertyStatus;
import com.backend.lab.domain.property.sale.entity.dto.SaleResp;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class PropertyStatusResp {

  private SaleResp sale;
  private LocalDate completedAt; // 완료일
  private LocalDate pendingAt; // 보류일
  private PropertyStatus status;
}
