package com.backend.lab.common.openapi.dto.floor;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FloorResp {
  private String floor; // 층번호명
  private Double areaPyung; //평
  private String mainPurps; // 주용도
  //기타용도
  private String etcPurps;
}
