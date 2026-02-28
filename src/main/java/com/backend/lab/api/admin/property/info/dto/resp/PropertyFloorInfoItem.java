package com.backend.lab.api.admin.property.info.dto.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyFloorInfoItem {

  private String floor; // 층
  private Double areaMeter; // 면적(제곱미터)
  private Double areaPyung; // 면적(평)
  private String upjong; // 업종 주용도 + (기타용도)

}
