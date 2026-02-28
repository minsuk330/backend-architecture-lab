package com.backend.lab.api.admin.property.core.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
public class PropertyAddressReq {

  @Schema(description = "필지고유번호")
  private String pnu; // 필지고유번호
  private String roadAddress;
  private String jibunAddress;

  @Schema(description = "위도")
  private Double lat; // 위도
  @Schema(description = "경도")
  private Double lng; // 경도
}
