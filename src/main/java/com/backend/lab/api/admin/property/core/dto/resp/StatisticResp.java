package com.backend.lab.api.admin.property.core.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class StatisticResp {
  //준비 완료 보류 매각
  private Long ready;
  private Long complete;
  private Long pending;
  private Long sold;

}
