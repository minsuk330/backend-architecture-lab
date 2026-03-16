package com.backend.lab.common.openapi.service.gong;

import com.backend.lab.common.openapi.dto.floor.BuildingFloorData;
import com.backend.lab.common.openapi.dto.floor.FloorApiResp;
import com.backend.lab.common.util.PnuComponents;
import com.backend.lab.api.admin.property.info.dto.resp.PropertyFloorInfoItem; // 추가
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class FloorApiService implements FloorApiPort {

  private final WebClient webClient;

  @Value("${building-register.api.key}")
  private String apiKey;

  @Value("${building-register.api.base-url}")
  private String baseUrl;


  @Async("buildingExecutor")
  public CompletableFuture<List<PropertyFloorInfoItem>> getFloorInfo(String pnu) {
    // 1. 브이월드 PNU를 공공데이터 API용으로 변환
    String publicDataPnu = PnuComponents.convertToPublicDataFormat(pnu);

    // 2. 변환된 PNU로 파싱
    PnuComponents pnuComponents = PnuComponents.parse(publicDataPnu);

    List<BuildingFloorData> buildingData = callBuildingFloorApi(pnuComponents);

    List<PropertyFloorInfoItem> result = buildingData.stream()
        .map(this::convertToPropertyFloorInfoItem)
        .toList();
    List<PropertyFloorInfoItem> sortedResult = sortFloorItems(result);

    return CompletableFuture.completedFuture(sortedResult);
  }

  private List<BuildingFloorData> callBuildingFloorApi(PnuComponents pnu) {
    String apiPath = "/getBrFlrOulnInfo";

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
        "&numOfRows=100" +
        "&pageNo=1";

      // URI 객체로 변환 (추가 인코딩 방지)
      URI uri = URI.create(fullUriString);

      FloorApiResp response = webClient.get()
          .uri(uri) // URI 객체 사용
          .retrieve()
          .bodyToMono(FloorApiResp.class)
          .timeout(Duration.ofSeconds(10))
          .block();

      if (response == null ||
          response.getResponse().getBody().getItems().getItem().isEmpty()) {
        throw new RuntimeException("건축물 정보를 찾을 수 없습니다");
      }

      return response.getResponse()
          .getBody()
          .getItems()
          .getItem();


  }

  private PropertyFloorInfoItem convertToPropertyFloorInfoItem(BuildingFloorData buildingData) {
    if (buildingData == null) {
      return null;
    }

    StringBuilder mainpurpose = new StringBuilder();

    String etcPurps = buildingData.getEtcPurps();
    if (StringUtils.hasText(etcPurps)) {
      mainpurpose.append(etcPurps);
    }

    Double area = buildingData.getArea();
    Double areaMeter = area != null ? Math.round(area * 100.0) / 100.0 : 0.0;
    Double pyung = 0.0;
    if (area != null && area > 0) {
      double pyungValue = area / 3.3058;
      pyung = Math.round(pyungValue * 100.0) / 100.0;
    }

    String flrNoNm = buildingData.getFlrNoNm();
    if (!StringUtils.hasText(flrNoNm)) {
      flrNoNm = "";
    }

    return PropertyFloorInfoItem.builder()
        .floor(flrNoNm)
        .areaMeter(areaMeter)
        .areaPyung(pyung)
        .upjong(mainpurpose.toString())
        .build();
  }
  private List<PropertyFloorInfoItem> sortFloorItems(List<PropertyFloorInfoItem> items) {
    return items.stream()
        .sorted(this::compareFloors)
        .toList();
  }

  private int compareFloors(PropertyFloorInfoItem item1, PropertyFloorInfoItem item2) {
    String floor1 = item1.getFloor();
    String floor2 = item2.getFloor();

    int type1 = getFloorType(floor1);
    int type2 = getFloorType(floor2);

    // 타입이 다르면 타입으로 역순 정렬 (지하=3, 지상=2, 옥탑=1)
    if (type1 != type2) {
      return Integer.compare(type2, type1);
    }

    int num1 = extractFloorNumber(floor1);
    int num2 = extractFloorNumber(floor2);

    // 지하층은 내림차순 (지2층이 지1층보다 위)
    if (type1 == 3) {
      return Integer.compare(num2, num1);
    }
    // 옥탑, 지상층은 오름차순 (낮은 층이 위)
    return Integer.compare(num1, num2);
  }

  private int getFloorType(String floor) {
    if (floor == null || floor.trim().isEmpty()) return 2;

    String normalized = floor.toLowerCase();
    if (normalized.contains("옥탑")) return 1;  // 옥탑
    if (normalized.contains("지하") || normalized.startsWith("지")) return 3;  // 지하
    return 2;  // 지상
  }

  private int extractFloorNumber(String floor) {
    if (floor == null || floor.trim().isEmpty()) return 0;

    // 숫자 추출
    String numbers = floor.replaceAll("[^0-9]", "");
    if (numbers.isEmpty()) return 0;

    try {
      return Integer.parseInt(numbers);
    } catch (NumberFormatException e) {
      return 0;
    }
  }
}