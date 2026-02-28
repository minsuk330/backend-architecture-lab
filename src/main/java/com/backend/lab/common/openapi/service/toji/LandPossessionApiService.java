package com.backend.lab.common.openapi.service.toji;

import com.backend.lab.common.openapi.dto.landPossession.LandPossessionApiResp;
import com.backend.lab.common.openapi.dto.landPossession.LandPossessionField;
import com.backend.lab.common.openapi.dto.landPossession.LandPossessionResp;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class LandPossessionApiService {

  private final WebClient webClient;

  @Value("${building-register.api.vworldKey}")
  private String apiKey;

  @Value("${building-register.api.landOwnerBaseUrl}")
  private String baseUrl;

  @Async("buildingExecutor")
  public CompletableFuture<LandPossessionResp> getLandOwnerInfo(String pnu) {

    LandPossessionApiResp apiResponse = callLandPossessionApi(pnu);

    LandPossessionResp landPossessionResp = convertToLandPossessionResp(apiResponse);

    return CompletableFuture.completedFuture(landPossessionResp);
  }


  private LandPossessionApiResp callLandPossessionApi(String pnu) {
    String fullUrl = UriComponentsBuilder.fromUri(URI.create(baseUrl))
        .queryParam("key", apiKey)
        .queryParam("domain", "kmorgan.co.kr")
        .queryParam("pnu", pnu)
        .queryParam("format", "json")
        .queryParam("numOfRows", "1000")
        .queryParam("pageNo", "1")
        .toUriString();

    LandPossessionApiResp response = webClient.get()
        .uri(fullUrl)
        .retrieve()
        .bodyToMono(LandPossessionApiResp.class)
        .timeout(Duration.ofSeconds(10))
        .block();

    if (response == null||response.getPossessions()==null) {
      throw new RuntimeException("토지이용계획 정보가 존재하지 않습니다.");
    }

    return response;
  }

  private LandPossessionResp convertToLandPossessionResp(LandPossessionApiResp apiResponse) {
    List<LandPossessionField> field = apiResponse.getPossessions().getField();

    LandPossessionField latestField = field.stream()
        .filter(data -> data.getOwnshipChgDe() != null && !data.getOwnshipChgDe().trim().isEmpty())
        .max((f1, f2) -> {
          try {
            // 날짜 문자열 비교 (YYYY-MM-DD 형식)
            return f1.getOwnshipChgDe().compareTo(f2.getOwnshipChgDe());
          } catch (Exception e) {
            return 0;
          }
        })
        .orElse(field.get(0)); // 날짜가 없으면 첫 번째 데이터 사용

    return LandPossessionResp.builder()
        .ownshipChgDe(latestField.getOwnshipChgDe())
        .posesnSeCodeNm(latestField.getPosesnSeCodeNm())
        .ownshipChgCauseCodeNm(latestField.getOwnshipChgCauseCodeNm())
        .build();
  }


}
