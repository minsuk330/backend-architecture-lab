package com.backend.lab.api.admin.property.core.dto.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeaseResp {

  @Schema(description = "층")
  private String floor; // 층

  @Schema(description = "면적(평)")
  private Double areaPyung; // 면적(평)

  @Schema(description = "면적(제곱미터)")
  private Double areaMeter; // 면적(제곱미터)

  @Schema(description = "업종")
  private String upjong; // 업종

  @Schema(description = "보증금")
  private Integer depositPrice; // 보증금
  @Schema(description = "월세")
  private Integer monthlyPrice; // 월세
  @Schema(description = "관리비")
  private Integer grPrice; // 관리비
  @Schema(description = "비고")
  @Column(length = 2000)
  private String etc; // 비고

  @Schema(description = "공실 여부")
  private Boolean isEmpty; // 공실
  @Schema(description = "숨김 여부")
  private Boolean isHidden; // 숨김

}
