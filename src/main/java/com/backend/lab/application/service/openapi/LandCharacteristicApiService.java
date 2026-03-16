package com.backend.lab.application.service.openapi;


import com.backend.lab.application.port.out.openapi.LandCharacteristicApiPort;
import com.backend.lab.common.openapi.dto.landCharacteristic.LandCharacteristicApiResp;
import com.backend.lab.common.openapi.dto.landCharacteristic.LandCharacteristicField;
import com.backend.lab.common.openapi.dto.landCharacteristic.LandCharacteristicsData;
import com.backend.lab.common.openapi.dto.landCharacteristic.LandCharacteristicResp;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class LandCharacteristicApiService implements LandCharacteristicApiPort {

  private final WebClient webClient;

  @Value("${building-register.api.vworldKey}")
  private String apiKey;

  @Value("${building-register.api.characterBaseUrl}")
  private String baseUrl;

  @Async("buildingExecutor")
  public CompletableFuture<LandCharacteristicResp> getLandCharacteristicInfo(String pnu) {
      // API 호출
      LandCharacteristicApiResp apiResponse = callLandCharacteristicsApi(pnu);

      LandCharacteristicResp result = convertToLandCharacteristicResp(apiResponse);

      return CompletableFuture.completedFuture(result);
  }

  private LandCharacteristicApiResp callLandCharacteristicsApi(String pnu) {
    // baseUrl을 사용하여 완전한 URL 구성
    String fullUrl = UriComponentsBuilder.fromUri(URI.create(baseUrl))
        .queryParam("key", apiKey)
        .queryParam("domain", "kmorgan.co.kr")
        .queryParam("pnu", pnu)
        .queryParam("stdrYear", "2024")  // 최신 연도로 설정
        .queryParam("format", "json")
        .queryParam("numOfRows", "1000")
        .queryParam("pageNo", "1")
        .toUriString();

    LandCharacteristicApiResp response = webClient.get()
        .uri(fullUrl)
        .retrieve()
        .bodyToMono(LandCharacteristicApiResp.class)
        .timeout(Duration.ofSeconds(10))
        .block();
    log.info("resultMsg:{}",response.getLandCharacteristicss().getResultMsg());


    if (response == null||response.getLandCharacteristicss() == null) {
      log.warn("토지특성 API 응답 없음 - PNU: {}, URL: {}", pnu, fullUrl);
      throw new RuntimeException("토지 특성 정보가 존재하지 않습니다.");
    }



    return response;
  }  private LandCharacteristicResp convertToLandCharacteristicResp(LandCharacteristicApiResp apiResponse) {
    LandCharacteristicsData data = apiResponse.getLandCharacteristicss();
    
    if (data.getField() == null || data.getField().isEmpty()) {
      return LandCharacteristicResp.builder().build();
    }

    LandCharacteristicField latestField = data.getField().stream()
        .filter(field -> field.getLastUpdtDt() != null && !field.getLastUpdtDt().trim().isEmpty())
        .max((f1, f2) -> {
          try {
            // 날짜 문자열 비교 (YYYY-MM-DD 형식이라 가정)
            return f1.getLastUpdtDt().compareTo(f2.getLastUpdtDt());
          } catch (Exception e) {
            return 0;
          }
        })
        .orElse(data.getField().get(0)); // 날짜가 없으면 첫 번째 데이터 사용

    log.info("선택된 데이터의 최종 업데이트 날짜: {}", latestField.getLastUpdtDt());

    // 숫자 변환 (안전한 파싱)
    Double landArea = parseDouble(latestField.getLndpclAr());
    Long officialPrice = parseLong(latestField.getPblntfPclnd());

    String dongJibun = getFormattedAddress(latestField.getLdCodeNm(), latestField.getMnnmSlno());

    // 계산된 값들
    Long pyengPrice = calculatePyengPrice(officialPrice);
    BigDecimal totalPrice = calculateTotalPrice(landArea, officialPrice);
    Double pyengLandArea = calculatePyengArea(landArea);

    return LandCharacteristicResp.builder()
        .landArea(landArea)
        .dongJibun(dongJibun)
        .pyengLandArea(pyengLandArea)
        .officialLandPrice(officialPrice)
        .pyengLandPrice(pyengPrice)
        .totalLandPrice(totalPrice)
        .landUseStatus(latestField.getLadUseSittnNm())
        .terrainHeight(latestField.getTpgrphHgCodeNm())
        .terrainShape(latestField.getTpgrphFrmCodeNm())
        .roadContact(latestField.getRoadSideCodeNm())
        .landCategoryName(latestField.getLndcgrCodeNm())
        .landUseZoneName1(latestField.getPrposArea1Nm())
        .landUseZoneName2(latestField.getPrposArea2Nm())
        .build();
  }
  public String getFormattedAddress(String ldCodeNm, String mnnmSlno) {
    if (ldCodeNm == null || mnnmSlno == null) {
      return null;
    }

    // ldCodeNm에서 동(洞) 이름만 추출 (마지막 공백 이후 문자열)
    String dongName = ldCodeNm.substring(ldCodeNm.lastIndexOf(" ") + 1);

    return dongName + " " + mnnmSlno + "번지";
  }

  private LandCharacteristicResp createDefaultResponse() {
    return LandCharacteristicResp.builder()
        .landArea(0.0)
        .officialLandPrice(0L)
        .pyengLandPrice(0L)
        .totalLandPrice(BigDecimal.ZERO)
        .landUseStatus("")
        .terrainHeight("")
        .terrainShape("")
        .roadContact("")
        .landCategoryName("")
        .landUseZoneName1("")
        .landUseZoneName2("")
        .build();
  }


  private Double parseDouble(String value) {
    try {
      return value != null && !value.trim().isEmpty() ? Double.parseDouble(value.trim()) : 0.00;
    } catch (NumberFormatException e) {
      return 0.0;
    }
  }

  private Long parseLong(String value) {
    try {
      return value != null && !value.trim().isEmpty() ? Long.parseLong(value.trim()) : 0L;
    } catch (NumberFormatException e) {
      return 0L;
    }
  }

  private Long calculatePyengPrice(Long pricePerM2) {
    return pricePerM2 != null ? Math.round(pricePerM2 * 3.3057785124) : 0L;
  }

  private Double calculatePyengArea(Double landArea) {
    if (landArea == null) {
      return 0.00;
    }
    double result = landArea / 3.3057785124;
    return Math.round(result * 100.0) / 100.0;
  }

  private BigDecimal calculateTotalPrice(Double area, Long pricePerM2) {
    if (area == null || pricePerM2 == null) {
      return BigDecimal.ZERO;
    }
    return BigDecimal.valueOf(area)
        .multiply(BigDecimal.valueOf(pricePerM2))
        .setScale(0, RoundingMode.HALF_UP);
  }

}
