package com.backend.lab.api.admin.property.info.dto.resp;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PropertyGonsiInfo {

  private String gonsiPrice; // 공시지가
  private String stdryear; // 기준년도
  private String percent;//이전대비

}
