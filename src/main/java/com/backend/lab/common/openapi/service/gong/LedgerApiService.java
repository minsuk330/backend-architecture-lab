package com.backend.lab.common.openapi.service.gong;

import com.backend.lab.api.admin.property.info.dto.BuildingInfo;
import com.backend.lab.common.openapi.dto.ledger.BuildingLedgerData;
import com.backend.lab.common.openapi.dto.ledger.LedgerApiResp;
import com.backend.lab.common.util.PnuComponents;
import com.backend.lab.domain.property.core.entity.embedded.LedgerProperties;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class LedgerApiService {

  private final WebClient webClient;

  @Value("${building-register.api.key}")
  private String apiKey;

  @Value("${building-register.api.base-url}")
  private String baseUrl;

  @Async("buildingExecutor")
  public CompletableFuture<List<LedgerProperties>> getLedgerInfo(String pnu, List<BuildingInfo> buildings) {
    // 브이월드 PNU를 공공데이터 API용으로 변환
    String publicDataPnu = PnuComponents.convertToPublicDataFormat(pnu);
    PnuComponents pnuComponents = PnuComponents.parse(publicDataPnu);


    Set<String> selectedMgmPks = buildings.stream()
        .map(BuildingInfo::getMgmBldrgstPk)
        .collect(Collectors.toSet());

    List<BuildingLedgerData> buildingData = callBuildingLedgerApi(pnuComponents);

    // 정확한 동등성 비교로 필터링
    List<BuildingLedgerData> filteredData = buildingData.stream()
        .filter(data -> selectedMgmPks.contains(data.getMgmBldrgstPk()))
        .toList();

    List<LedgerProperties> ledgerProperties = filteredData.stream()
        .map(data -> {
          LedgerProperties properties = convertToLedgerProperties(data);
          properties.setPnu(pnu);
          return properties;
        })
        .toList();

    return CompletableFuture.completedFuture(ledgerProperties);
  }
  //건물 추가 모달
  @Async("buildingExecutor")
  public CompletableFuture<List<BuildingInfo>> getBuildingInfo(String pnu) {
    // 브이월드 PNU를 공공데이터 API용으로 변환
    String publicDataPnu = PnuComponents.convertToPublicDataFormat(pnu);
    PnuComponents pnuComponents = PnuComponents.parse(publicDataPnu);

    List<BuildingLedgerData> buildingData = callBuildingLedgerApi(pnuComponents);
    List<BuildingInfo> result = buildingData.stream()
        .map(this::convertToBuildingInfo)
        .toList();

    return CompletableFuture.completedFuture(result);
  }

  private List<BuildingLedgerData> callBuildingLedgerApi(PnuComponents pnu) {
    String apiPath = "/getBrTitleInfo";

    // API 키 수동 인코딩
    String encodedApiKey = apiKey
        .replace("+", "%2B")
        .replace("/", "%2F")
        .replace("=", "%3D");
    
    // URI를 직접 문자열로 구성
    String fullUriString = baseUrl + apiPath + 
        "?serviceKey=" + encodedApiKey +
        "&sigunguCd=" + pnu.getSigunguCd() +
        "&bjdongCd=" + pnu.getBjdongCd() +
        "&platGbCd=" + pnu.getMountainYn() +
        "&bun=" + pnu.getBun() +
        "&ji=" + pnu.getJi() +
        "&_type=json" +
        "&numOfRows=1000" +
        "&pageNo=1";

      // URI 객체로 변환 (추가 인코딩 방지)
      URI uri = URI.create(fullUriString);
      
      LedgerApiResp response = webClient.get()
          .uri(uri) // URI 객체 사용
          .retrieve()
          .bodyToMono(LedgerApiResp.class)
          .timeout(Duration.ofSeconds(10))
          .block();

      if (response == null || response.getResponse().getBody().getItems().getItem().isEmpty()) {
        throw new RuntimeException("건축물 정보를 찾을 수 없습니다");
      }

      return response.getResponse().getBody().getItems().getItem();

  }


  private BuildingInfo convertToBuildingInfo(BuildingLedgerData data) {
    return BuildingInfo.builder()
        .dongNm(data.getDongNm())
        .hoNm("-")
        .buildingName(data.getBldNm())
        .mgmBldrgstPk(data.getMgmBldrgstPk())
        .jibunAddress(data.getPlatPlc())
        .build();
  }

  public String extractDongAndBunji(String fullAddress) {
    if (fullAddress == null || fullAddress.trim().isEmpty()) {
      return "";
    }

    String[] parts = fullAddress.trim().split("\\s+");

    // 2번째 공백부터 끝까지 (인덱스 2부터)
    if (parts.length > 2) {
      return String.join(" ", Arrays.copyOfRange(parts, 2, parts.length));
    }

    return fullAddress; // 공백이 2개 미만이면 원본 반환
  }

  private LedgerProperties convertToLedgerProperties(BuildingLedgerData data) {
    LedgerProperties properties = new LedgerProperties();

    // 층 정보
    Integer undergroundFloor = parseIntOrNull(data.getUgrndFlrCnt());
    properties.setMinFloor(undergroundFloor != null ? undergroundFloor : 0);
    properties.setMaxFloor(parseIntOrNull(data.getGrndFlrCnt()));

    //주소 정보
    properties.setJibunAddress(data.getPlatPlc());
    properties.setRoadAddress(data.getNewPlatPlc());
    properties.setDongNm(data.getDongNm());
    properties.setDongJibun(extractDongAndBunji(data.getPlatPlc()));


    // 용도 및 구조
    properties.setMainPurpsCdNm(data.getMainPurpsCdNm());
    properties.setStructure(data.getStrctCdNm());
    properties.setEtcUsage(data.getEtcPurps());
    properties.setJiboong(data.getRoofCdNm());

    // 면적 정보 (㎡와 평 모두 설정)
    setAreaInfo(properties, data);

    // 승강기
    properties.setIlbanElevator(parseIntOrNull(data.getRideUseElvtCnt()));
    properties.setHwamoolElevator(parseIntOrNull(data.getEmgenUseElvtCnt()));

    // 도로너비 (API에서 제공하지 않으므로 null)
    properties.setDoroWidthMeter(null);

    // 주차 - 자주식
    properties.setJajuInParking(parseIntOrNull(data.getIndrAutoUtcnt()));
    properties.setJajuOutParking(parseIntOrNull(data.getOudrAutoUtcnt()));

    // 주차 - 기계식
    properties.setKigyeInParking(parseIntOrNull(data.getIndrMechUtcnt()));
    properties.setKigyeOutParking(parseIntOrNull(data.getOudrMechUtcnt()));

    // 주차 - 전기차 (API에서 제공하지 않으므로 null)
    properties.setElcInParking(null);
    properties.setElcOutParking(null);

    // 건폐율, 용적률
    properties.setGeonPye(parseDoubleOrNull(data.getBcRat()));
    properties.setYongjeok(parseDoubleOrNull(data.getVlRat()));

    // 소유자 (API에서 제공하지 않음)
    properties.setOwner(null);

    // 세대수, 가구수
    properties.setSaedae(parseIntOrNull(data.getHhldCnt()));
    properties.setGagu(parseIntOrNull(data.getFmlyCnt()));

    // 높이
    properties.setHeight(parseDoubleOrNull(data.getHeit()));

    // 일자 정보
    properties.setHeogaeDate(parseDateOrNull(data.getPmsDay()));
    properties.setChakGongDate(parseDateOrNull(data.getStcnsDay()));
    properties.setSengInDate(parseDateOrNull(data.getUseAprDay()));
    properties.setRemodelDate(null); // API에서 제공하지 않음

    return properties;
  }

  private void setAreaInfo(LedgerProperties properties, BuildingLedgerData data) {
    // 대지면적
    //Double landAreaMeter = parseDoubleOrNull(data.getPlatArea());
    if (data.getPlatArea() != null) {
      properties.setLandAreaMeter(data.getPlatArea());
      properties.setLandAreaPyeong(meterToPyeong(data.getPlatArea()));
    }

    // 건축면적
    //Double buildingAreaMeter = parseDoubleOrNull(data.getArchArea());
    if (data.getArchArea() != null) {
      properties.setBuildingAreaMeter(data.getArchArea());
      properties.setBuildingAreaPyeong(meterToPyeong(data.getArchArea()));
    }

    // 연면적
    //Double yeonAreaMeter = parseDoubleOrNull(data.getTotArea());
    if (data.getTotArea() != null) {
      properties.setYeonAreaMeter(data.getTotArea());
      properties.setYeonAreaPyeong(meterToPyeong(data.getTotArea()));
    }

    // 용적률산정연면적
    //Double yongjeokAreaMeter = parseDoubleOrNull(data.getVlRatEstmTotArea());
    if (data.getVlRatEstmTotArea() != null) {
      properties.setYongjeokAreaMeter(data.getVlRatEstmTotArea());
      properties.setYongjeokAreaPyeong(meterToPyeong(data.getVlRatEstmTotArea()));
    }
  }

  private Double parseDoubleOrNull(String str) {
    try {
      return str != null && !str.trim().isEmpty() ? Double.parseDouble(str) : null;
    } catch (NumberFormatException e) {
      return null;
    }
  }

  private Integer parseIntOrNull(String str) {
    try {
      return str != null && !str.trim().isEmpty() ? Integer.parseInt(str) : null;
    } catch (NumberFormatException e) {
      return null;
    }
  }

  private LocalDate parseDateOrNull(String str) {
    try {
      if (str == null || str.trim().isEmpty()) return null;
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
      return LocalDate.parse(str, formatter);
    } catch (Exception e) {
      return null;
    }
  }

  private Double meterToPyeong(Double meter) {
    if (meter == null) {
      return 0.00;
    }
    double result = meter / 3.3057785124;
    return Math.round(result * 100.0) / 100.0;
  }

}
