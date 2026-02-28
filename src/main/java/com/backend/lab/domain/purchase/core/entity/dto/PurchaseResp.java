package com.backend.lab.domain.purchase.core.entity.dto;

import com.backend.lab.domain.purchase.core.entity.vo.PurchaseMethod;
import com.backend.lab.domain.purchase.core.entity.vo.PurchaseStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseResp {

  private LocalDateTime purchasedAt;
  private String orderNumber;
  private String name;
  private Integer totalPrice;
  private Integer amount;
  private Long id;
  private String buyerName;
  private PurchaseMethod method;
  private PurchaseStatus status;
}
