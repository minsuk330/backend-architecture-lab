package com.backend.lab.api.admin.property.core.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyStatResp {

  //전체 누적통계
  private StatisticResp total;
  //당월 누적통계
  private StatisticResp monthly;

  private Long totalPropertyCount;
  private Long toDayPropertyCount;
  private Long yesterdayPropertyCount;
  private Long monthlyPropertyCount;
}
