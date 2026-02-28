package com.backend.lab.domain.property.core.entity.embedded;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
@Schema(description = "토지 정보")
public class LandProperties {

  private String pnu;

  @Schema(description = "동+지번")
  private String dongJibun;

  private String jibunAddress;

  @Schema(description = "면적(제곱미터)")
  private Double areaMeter; // 면적 (제곱미터)
  @Schema(description = "면적(평)")
  private Double areaPyung; // 면적 (평)

  @Schema(description = "공시지가 (제곱미터당 가격)")
  private Long gonsiPricePerMeter; // 공시지가 (제곱미터당 가격)
  @Schema(description = "공시지가 (평당 가격)")
  private Long gonsiPricePerPyung; // 공시지가 (평당 가격)
  @Schema(description = "총 공시지가")
  private BigDecimal totalGonsiPrice; // 총 공시지가

  @Schema(description = "지목")
  private String jimok; // 지목
  @Schema(description = "용도 지역")
  private String yongdo; // 용도지역
  @Schema(description = "이용 상황")
  private String iyongSituation; // 이용상황

  @Schema(description = "소유자 유형")
  private String ownerType; // 소유자 유형 (개인, 법인 등)
  @Schema(description = "소유권 이전일")
  private LocalDate ownerChangeDate; // 소유권 이전일
  @Schema(description = "소유권 변동 원인")
  private String ownerChangeReason; // 소유권 변동 원인

  @Schema(description = "도로접면")
  private String doroJeopMyun; // 도로접면
  @Schema(description = "지형 높이")
  private String landHeight; // 지형 높이
  @Schema(description = "지형 형상")
  private String landShape; // 지형 형상

  @Schema(description = "국계법, 포함에 해당되는 토지 이용 계획 (,로 구분)")
  private String phUsagePlanWithLaw; // 국계법, 포함에 해당되는 토지 이용 계획 ,로 구분
  @Schema(description = "국계법, 저촉에 해당되는 토지 이용 계획 (,로 구분)")
  private String jcUsagePlanWithLaw; // 국계법, 저촉에 해당되는 토지 이용 계획 ,로 구분
  @Schema(description = "국계법, 접함에 해당되는 토지 이용 계획 (,로 구분)")
  private String jhUsagePlanWithLaw; // 국계법, 접함에 해당되는 토지 이용 계획 ,로 구분

  @Schema(description = "기타 법률, 포함에 해당되는 토지 이용 계획 (,로 구분)")
  private String phUsagePlanWithEtc; // 기타 법률, 포함에 해당되는 토지 이용 계획 ,로 구분
  @Schema(description = "기타 법률, 저촉에 해당되는 토지 이용 계획 (,로 구분)")
  private String jcUsagePlanWithEtc; // 기타 법률, 저촉에 해당되는 토지 이용 계획 ,로 구분
  @Schema(description = "기타 법률, 접함에 해당되는 토지 이용 계획 (,로 구분)")
  private String jhUsagePlanWithEtc; // 기타 법률, 접함에 해당되는 토지 이용 계획 ,로 구분

  public LandProperties update(LandProperties other) {
    if (other == null) {
      return this;
    }

    if (other.getDongJibun() != null) {
      this.dongJibun = other.getDongJibun();
    }

    if (other.getAreaMeter() != null) {
      this.areaMeter = other.getAreaMeter();
    }
    if (other.getAreaPyung() != null) {
      this.areaPyung = other.getAreaPyung();
    }
    if (other.getGonsiPricePerMeter() != null) {
      this.gonsiPricePerMeter = other.getGonsiPricePerMeter();
    }
    if (other.getGonsiPricePerPyung() != null) {
      this.gonsiPricePerPyung = other.getGonsiPricePerPyung();
    }
    if (other.getTotalGonsiPrice() != null) {
      this.totalGonsiPrice = other.getTotalGonsiPrice();
    }
    if (other.getJimok() != null) {
      this.jimok = other.getJimok();
    }
    if (other.getYongdo() != null) {
      this.yongdo = other.getYongdo();
    }
    if (other.getIyongSituation() != null) {
      this.iyongSituation = other.getIyongSituation();
    }
    if (other.getOwnerType() != null) {
      this.ownerType = other.getOwnerType();
    }
    if (other.getOwnerChangeDate() != null) {
      this.ownerChangeDate = other.getOwnerChangeDate();
    }
    if (other.getOwnerChangeReason() != null) {
      this.ownerChangeReason = other.getOwnerChangeReason();
    }
    if (other.getDoroJeopMyun() != null) {
      this.doroJeopMyun = other.getDoroJeopMyun();
    }
    if (other.getLandHeight() != null) {
      this.landHeight = other.getLandHeight();
    }
    if (other.getLandShape() != null) {
      this.landShape = other.getLandShape();
    }
    if (other.getPhUsagePlanWithLaw() != null) {
      this.phUsagePlanWithLaw = other.getPhUsagePlanWithLaw();
    }
    if (other.getJcUsagePlanWithLaw() != null) {
      this.jcUsagePlanWithLaw = other.getJcUsagePlanWithLaw();
    }
    if (other.getJhUsagePlanWithLaw() != null) {
      this.jhUsagePlanWithLaw = other.getJhUsagePlanWithLaw();
    }
    if (other.getPhUsagePlanWithEtc() != null) {
      this.phUsagePlanWithEtc = other.getPhUsagePlanWithEtc();
    }
    if (other.getJcUsagePlanWithEtc() != null) {
      this.jcUsagePlanWithEtc = other.getJcUsagePlanWithEtc();
    }
    if (other.getJhUsagePlanWithEtc() != null) {
      this.jhUsagePlanWithEtc = other.getJhUsagePlanWithEtc();
    }
    return this;
  }
}
