package com.backend.lab.common.openapi.dto.gonsiPrice;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GonsiPriceItem {
  /**
   * 본번-부번 (예: "953-11")
   */
  @JsonProperty("mnnmSlno")
  private String mnnmSlno;

  /**
   * 공시일자 (예: "1990-08-30")
   */
  @JsonProperty("pblntfDe")
  private String pblntfDe;

  /**
   * 표준지여부 (Y/N)
   */
  @JsonProperty("stdLandAt")
  private String stdLandAt;

  /**
   * 기준년도 (예: "1990")
   */
  @JsonProperty("stdrYear")
  private String stdrYear;

  /**
   * PNU 코드 (예: "1168011800109530011")
   */
  @JsonProperty("pnu")
  private String pnu;

  /**
   * 최종수정일자 (예: "2023-08-10")
   */
  @JsonProperty("lastUpdtDt")
  private String lastUpdtDt;

  /**
   * 법정동코드 (예: "1168011800")
   */
  @JsonProperty("ldCode")
  private String ldCode;

  /**
   * 법정동명 (예: "서울특별시 강남구 도곡동")
   */
  @JsonProperty("ldCodeNm")
  private String ldCodeNm;

  /**
   * 등록구분코드명 (예: "일반")
   */
  @JsonProperty("regstrSeCodeNm")
  private String regstrSeCodeNm;

  /**
   * 기준월 (예: "01")
   */
  @JsonProperty("stdrMt")
  private String stdrMt;

  /**
   * 공시지가 (원/㎡)
   */
  @JsonProperty("pblntfPclnd")
  private String pblntfPclnd;

  /**
   * 등록구분코드 (예: "1")
   */
  @JsonProperty("regstrSeCode")
  private String regstrSeCode;
}
