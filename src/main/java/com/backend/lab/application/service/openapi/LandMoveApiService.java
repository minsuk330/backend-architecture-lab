package com.backend.lab.application.service.openapi;

import com.backend.lab.api.admin.property.info.dto.resp.PropertyMoveInfoItem;
import com.backend.lab.application.port.out.openapi.LandMoveApiPort;
import com.backend.lab.common.openapi.dto.landMove.LandMoveField;
import com.backend.lab.common.openapi.dto.landMove.LandMovesResponse;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class LandMoveApiService implements LandMoveApiPort {

  private final WebClient moveWebClient;

  @Value("${building-register.api.vworldKey}")
  private String apiKey;

  @Value("${building-register.api.moveBaseUrl}")
  private String baseUrl;

  @Async("buildingExecutor")
  public CompletableFuture<List<PropertyMoveInfoItem>> getLandMovesInfo(String pnu) {

    List<LandMoveField> landMoveData = callLandMovesApi(pnu);

    List<PropertyMoveInfoItem> result = landMoveData.stream()
        .map(this::convertToPropertyLandMoveInfoItem)
        .toList();

    return CompletableFuture.completedFuture(result);
  }

  private List<LandMoveField> callLandMovesApi(String pnu) {
    // 날짜 범위 설정 (기본값: 1948년부터 현재까지)
    String startDt = "19480501";
    String endDt = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

    String fullUrl = UriComponentsBuilder.fromUri(URI.create(baseUrl))
        .queryParam("key", apiKey)
        .queryParam("domain", "kmorgan.co.kr")
        .queryParam("pnu", pnu)
        .queryParam("startDt", startDt)
        .queryParam("endDt", endDt)
        .queryParam("format", "json")
        .queryParam("numOfRows", "1000")
        .queryParam("pageNo", "1")
        .toUriString();

    LandMovesResponse response = moveWebClient.get()
        .uri(fullUrl)
        .retrieve()
        .bodyToMono(LandMovesResponse.class)
        .timeout(Duration.ofSeconds(10))
        .block();

    if (response == null ||
        response.getLandMoves() == null ||
        response.getLandMoves().getField() == null ||
        response.getLandMoves().getField().isEmpty()) {
      throw new RuntimeException("토지 이동 정보를 찾을 수 없습니다");
    }

    return response.getLandMoves().getField();
  }


  private PropertyMoveInfoItem convertToPropertyLandMoveInfoItem(LandMoveField landMoveData) {
    if (landMoveData == null) {
      return null;
    }

    String moveDate = landMoveData.getLadMvmnDe();
    if (!StringUtils.hasText(moveDate)) {
      moveDate = "";
    }

    String moveReason = landMoveData.getLadMvmnPrvonshCodeNm();
    if (!StringUtils.hasText(moveReason)) {
      moveReason = "";
    }

    return PropertyMoveInfoItem.builder()
        .moveDate(moveDate)
        .moveReason(moveReason)
        .build();
  }



}
