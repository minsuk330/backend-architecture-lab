package com.backend.lab.domain.property.sale.entity.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SaleResp {

  private LocalDate saleAt;
  private Long memberId;
  private Long propertyId;

  private Long salePrice;
  private Long earningPrice;

}
