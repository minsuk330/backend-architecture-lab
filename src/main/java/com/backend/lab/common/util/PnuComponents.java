package com.backend.lab.common.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PnuComponents {
  private String fullPnu;      // 전체 PNU (19자리)
  private String sigunguCd;    // 시군구코드 (5자리)
  private String bjdongCd;     // 법정동코드 (5자리)
  private String mountainYn;        // 산 여부 (1자리)
  private String bun;          // 번 (4자리)
  private String ji;           // 지 (4자리)

  public static PnuComponents parse(String pnu) {
    if (pnu == null || pnu.length() != 19) {
      throw new IllegalArgumentException("PNU는 19자리여야 합니다.");
    }

    return PnuComponents.builder()
        .fullPnu(pnu)
        .sigunguCd(pnu.substring(0, 5))      // 1-5자리: 시군구코드
        .bjdongCd(pnu.substring(5, 10))      // 6-10자리: 법정동코드
        .mountainYn(pnu.substring(10, 11))   // 11자리: 산 여부 (1:대지, 2:산)
        .bun(pnu.substring(11, 15))          // 12-15자리: 번
        .ji(pnu.substring(15, 19))           // 16-19자리: 지
        .build();
  }

  /**
   * 브이월드 PNU를 공공데이터 포맷으로 변환 (공공데이터 API 호출용)
   * 브이월드: 1=평지, 2=산지
   * 공공데이터: 0=평지, 1=산지
   * @param vWorldPnu 브이월드 기반 PNU
   * @return 공공데이터 포맷 PNU
   */
  public static String convertToPublicDataFormat(String vWorldPnu) {
    if (vWorldPnu == null || vWorldPnu.length() != 19) {
      throw new IllegalArgumentException("PNU는 19자리여야 합니다.");
    }

    String mountainCode = vWorldPnu.substring(10, 11);
    String convertedMountainCode;

    // 산 여부 코드 변환: 브이월드 → 공공데이터
    switch (mountainCode) {
      case "1": // 브이월드 평지
        convertedMountainCode = "0"; // 공공데이터 평지
        break;
      case "2": // 브이월드 산지
        convertedMountainCode = "1"; // 공공데이터 산지
        break;
      default:
        throw new IllegalArgumentException("유효하지 않은 산 여부 코드: " + mountainCode);
    }

    // 변환된 PNU 생성
    return vWorldPnu.substring(0, 10) + convertedMountainCode + vWorldPnu.substring(11);
  }


}
