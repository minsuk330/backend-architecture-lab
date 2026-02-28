package com.backend.lab.api.admin.property.core.dto.req;

import com.backend.lab.domain.property.core.entity.vo.PropertyStatus;
import com.backend.lab.domain.property.sale.entity.dto.SaleReq;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class PropertyStatusUpdateReq {

  private SaleReq sale;
  private LocalDate completedAt; // 완료일
  private LocalDate pendingAt; // 보류일
  private PropertyStatus updateStatus;

}
