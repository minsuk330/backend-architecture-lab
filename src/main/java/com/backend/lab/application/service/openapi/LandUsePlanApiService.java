package com.backend.lab.common.openapi.service.toji;

import com.backend.lab.common.openapi.dto.landUsePlan.LandUseField;
import com.backend.lab.common.openapi.dto.landUsePlan.LandUsesApiResp;
import com.backend.lab.common.openapi.dto.landUsePlan.LandUseResp;
import com.backend.lab.common.openapi.dto.landUsePlan.LandUseResp.Item;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class LandUsePlanApiService implements LandUsePlanApiPort {

  private final WebClient webClient;

  @Value("${building-register.api.vworldKey}")
  private String apiKey;

  @Value("${building-register.api.landUsePlanBaseUrl}")
  private String baseUrl;

  @Async("buildingExecutor")
  public CompletableFuture<LandUseResp> getLandUsePlanInfo(String pnu) {
    LandUsesApiResp landUsesApiResp = callLandUsePlanApi(pnu);

    LandUseResp landUseResp = convertToLandUseResp(landUsesApiResp);

    return CompletableFuture.completedFuture(landUseResp);
  }

  private LandUsesApiResp callLandUsePlanApi(String pnu) {
    String fullUrl = UriComponentsBuilder.fromUri(URI.create(baseUrl))
        .queryParam("key", apiKey)
        .queryParam("domain", "kmorgan.co.kr")
        .queryParam("pnu", pnu)
        .queryParam("format", "json")
        .queryParam("numOfRows", "1000")
        .queryParam("pageNo", "1")
        .toUriString();

    LandUsesApiResp response = webClient.get()
        .uri(fullUrl)
        .retrieve()
        .bodyToMono(LandUsesApiResp.class)
        .timeout(Duration.ofSeconds(10))
        .block();

    if (response == null||response.getLandUses()==null) {
      throw new RuntimeException("토지이용계획 정보가 존재하지 않습니다.");
    }

    return response;
  }

  private LandUseResp convertToLandUseResp(LandUsesApiResp apiResponse) {

    List<LandUseField> field = apiResponse.getLandUses().getField();

    List<Item> nationalLawItem = new ArrayList<>();
    List<Item> otherLawItem = new ArrayList<>();

    for (LandUseField landUseField : field) {
      LandUseResp.Item item = Item.builder()
          .name(landUseField.getPrposAreaDstrcCodeNm())
          .constructableStatus(landUseField.getCnflcAtNm())
          .build();

      if (NATIONAL_LAW_ITEMS.contains(item.getName())) {
        nationalLawItem.add(item);
      }
      else {
        otherLawItem.add(item);
      }
    }
  return LandUseResp.builder()
        .otherLawItems(otherLawItem)
        .nationalLawItems(nationalLawItem)
        .build();
  }



  private static final Set<String> NATIONAL_LAW_ITEMS = Set.of(
      "도시지역", "관리지역", "농림지역", "자연환경보전지역",
      "주거지역", "상업지역", "공업지역", "녹지지역",
      "제1종일반주거지역", "제2종일반주거지역", "제3종일반주거지역", "준주거지역",
      "중심상업지역", "일반상업지역", "유통상업지역", "준공업지역",
      "지구단위계획구역", "개발제한구역", "시가화조정구역", "도시자연공원구역",
      "도로", "광장", "공원", "학교", "주차장", "하천", "도시계획시설"
  );


}
