package com.backend.lab.api.admin.property.core.dto.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyLandResp {

  private Long buildingOrder;

  private String jibunAddress;

  private Double areaMeter; // 면적 (제곱미터)
  private Double areaPyung; // 면적 (평)

  private Long gonsiPricePerMeter; // 공시지가 (제곱미터당 가격)
  private Long gonsiPricePerPyung; // 공시지가 (평당 가격)
  private BigDecimal totalGonsiPrice; // 총 공시지가

  private String jimok; // 지목
  private String yongdo; // 용도지역
  private String iyongSituation; // 이용상황

  private String ownerType; // 소유자 유형 (개인, 법인 등)
  private LocalDate ownerChangeDate; // 소유권 이전일
  private String ownerChangeReason; // 소유권 변동 원인

  private String doroJeopMyun; // 도로접면
  private String landHeight; // 지형 높이
  private String landShape; // 지형 형상

  private String phUsagePlanWithLaw; // 국계법, 포함에 해당되는 토지 이용 계획 ,로 구분
  private String jcUsagePlanWithLaw; // 국계법, 저촉에 해당되는 토지 이용 계획 ,로 구분
  private String jhUsagePlanWithLaw; // 국계법, 접함에 해당되는 토지 이용 계획 ,로 구분

  private String phUsagePlanWithEtc; // 기타 법률, 포함에 해당되는 토지 이용 계획 ,로 구분
  private String jcUsagePlanWithEtc; // 기타 법률, 저촉에 해당되는 토지 이용 계획 ,로 구분
  private String jhUsagePlanWithEtc; // 기타 법률, 접함에 해당되는 토지 이용 계획 ,로 구분


}
