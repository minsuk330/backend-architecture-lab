package com.backend.lab.common.openapi.service.kakao;

import com.backend.lab.common.openapi.service.kakao.dto.KakaoAddressSearchResponse;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoApiService {

  private final WebClient webClient;
  @Value("${building-register.api.kakaoKey}")
  private String apiKey;

  public KakaoAddressSearchResponse searchAddress(String query) {
    URI uri = UriComponentsBuilder
        .fromUriString("https://dapi.kakao.com/v2/local/search/address.json")
        .queryParam("analyze_type", "similar")
        .queryParam("page", 1)
        .queryParam("size", 10)
        .queryParam("query", query)
        .build()
        .encode(StandardCharsets.UTF_8)
        .toUri();
    log.info("카카오 API 요청 URL: {}", uri);
    log.info("카카오 API Key: KakaoAK {}", apiKey);

    try{
      KakaoAddressSearchResponse response = webClient.get()
          .uri(uri)
          .header("Authorization", "KakaoAK " + apiKey)
          .header("Content-Type", "application/json")
          .header("Accept", "*/*")
          .retrieve()
          .bodyToMono(KakaoAddressSearchResponse.class)
          .timeout(Duration.ofSeconds(10))
          .block();

      if (response == null||response.getDocuments().isEmpty()) {
        throw new RuntimeException("aaa");
      }

      return response;
    }
    catch (Exception e){
      throw new RuntimeException("카카오 실패", e);
    }

  }
}
