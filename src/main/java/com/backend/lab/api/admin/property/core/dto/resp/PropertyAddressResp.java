package com.backend.lab.api.admin.property.core.dto.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PropertyAddressResp {

  private String pnu;

  private String roadAddress;
  private String jibunAddress;

  private Double lat; // 위도

  private Double lng; // 경도
}
