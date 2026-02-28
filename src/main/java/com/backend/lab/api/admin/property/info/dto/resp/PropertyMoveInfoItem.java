package com.backend.lab.api.admin.property.info.dto.resp;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyMoveInfoItem {
  private String moveDate;     // 토지이동일자
  private String moveReason;   // 토지이동사유

}
