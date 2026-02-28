package com.backend.lab.api.admin.property.core.dto.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PropertyPriceResp {
  @Schema(description = "매매가")
  private Long mmPrice;

  private Long igPrice;
  @Schema(description = "기타매매가")
  private Long etcPrice;
  @Schema(description = "수익률")
  private Double roi;

  @Schema(description = "대지면적 평단가")
  private Long pyeongPrice; // 평당가
  //추가한거
  @Schema(description = "연면적 평단가")
  private Long yeonPyongPrice;
  @Schema(description = "보증금")
  private Long depositPrice; // 보증금
  @Schema(description = "월임대료")
  private Long monthPrice; // 월임대료
  @Schema(description = "관리비")
  private Long grPrice; // 관리비

  @Schema(description = "관리비 지출")
  private Long grOut; // 관리비 지출
  @Schema(description = "관리비 기타")
  private Long grEtc; // 관리비 기타

  @Schema(description = "대출 현황")
  @Column(length = 2000)
  private String loanDescription; // 대출 현황
}
