package com.backend.lab.domain.property.core.entity.embedded;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@Builder
@Schema(description = "금액 정보")
public class PriceProperties {

  @Schema(description = "수수료 타입")
  private String susuType; // 수수료 타입
  @Schema(description = "수수료 비율")
  private Long susuRate; // 수수료 비율

  @Schema(description = "매매가")
  private Long mmPrice; // 매매가
  private Long igPrice; // 입금
  @Schema(description = "기타매매가")
  private Long etcPrice; // 기타 매매가
  @Schema(description = "수익률")
  private Double roi; // 수익률

  @Schema(description = "대지면적 평당가")
  private Long pyeongPrice; // 평당가, 대지면적 평단가
  //연면적 평단가 추가
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

  public PriceProperties update(PriceProperties other) {
    if(other == null) {
      return this;
    }

    if(other.getSusuType() != null) {
      this.susuType = other.getSusuType();
    }
    if(other.getSusuRate() != null) {
      this.susuRate = other.getSusuRate();
    }
    if(other.getMmPrice() != null) {
      this.mmPrice = other.getMmPrice();
    }
    if(other.getIgPrice() != null) {
      this.igPrice = other.getIgPrice();
    }
    if(other.getEtcPrice() != null) {
      this.etcPrice = other.getEtcPrice();
    }
    if(other.getRoi() != null) {
      this.roi = other.getRoi();
    }
    if(other.getPyeongPrice() != null) {
      this.pyeongPrice = other.getPyeongPrice();
    }
    if(other.getDepositPrice() != null) {
      this.depositPrice = other.getDepositPrice();
    }
    if(other.getMonthPrice() != null) {
      this.monthPrice = other.getMonthPrice();
    }
    if(other.getGrPrice() != null) {
      this.grPrice = other.getGrPrice();
    }
    if(other.getGrOut() != null) {
      this.grOut = other.getGrOut();
    }
    if(other.getGrEtc() != null) {
      this.grEtc = other.getGrEtc();
    }
    if(other.getLoanDescription() != null) {
      this.loanDescription = other.getLoanDescription();
    }
    return this;
  }

}
