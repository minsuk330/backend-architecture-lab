package com.backend.lab.common.oauth.service;

import com.backend.lab.common.oauth.dto.OauthUserInfo;
import com.backend.lab.common.oauth.properties.KakaoOauthProperties;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class KakaoOauthService implements OauthService {

  private final WebClient webClient;
  private final KakaoOauthProperties kakaoOauthProperties;

  @Override
  public URI getLoginURI(String redirectUri) {
    return UriComponentsBuilder.fromUri(URI.create(kakaoOauthProperties.getAuthorizationUrl()))
        .queryParam("client_id", kakaoOauthProperties.getClientId())
        .queryParam("response_type", "code")
        .queryParam("redirect_uri", kakaoOauthProperties.getCallbackUrl())
        .queryParam("state", URLEncoder.encode(redirectUri, StandardCharsets.UTF_8))
        .build()
        .toUri();
  }

  @Override
  public OauthUserInfo getProviderId(String payload, String state) {

    final String baseUrl = "https://kauth.kakao.com/oauth/token";

    String contentType = "application/x-www-form-urlencoded;charset=utf-8";
    String grantType = "authorization_code";
    String clientId = kakaoOauthProperties.getClientId();
    String clientSecret = kakaoOauthProperties.getClientSecret();
    String redirectUri = kakaoOauthProperties.getCallbackUrl();
    String code = payload;

    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.add("grant_type", grantType);
    formData.add("client_id", clientId);
    formData.add("redirect_uri", redirectUri);
    formData.add("client_secret", clientSecret);
    formData.add("code", code);
    formData.add("state", state);

    String responseRaw = webClient.post()
        .uri(baseUrl)
        .header("Content-Type", contentType)
        .bodyValue(formData)
        .retrieve()
        .bodyToMono(String.class)
        .block();
    JsonObject response = JsonParser.parseString(responseRaw).getAsJsonObject();
    if (response == null || response.isJsonNull()) {
      throw new RuntimeException("Invalid response from Kakao OAuth");
    }

    if (!response.has("access_token")) {
      throw new RuntimeException("Invalid response from Kakao OAuth: " + response);
    }

    String accessToken = response.get("access_token").getAsString();

    final String userInfoUrl = "https://kapi.kakao.com/v2/user/me";
    String authorizationHeader = "Bearer " + accessToken;

    String userInfoResponseRaw = webClient.get()
        .uri(userInfoUrl)
        .header("Content-Type", contentType)
        .header("Authorization", authorizationHeader)
        .retrieve()
        .bodyToMono(String.class)
        .block();

    JsonObject userInfoResponse = JsonParser.parseString(userInfoResponseRaw).getAsJsonObject();
    if (userInfoResponse == null || userInfoResponse.isJsonNull()) {
      throw new RuntimeException("Invalid response from Kakao User Info API");
    }

    if (!userInfoResponse.has("id")) {
      throw new RuntimeException("Invalid response from Kakao User Info API: " + userInfoResponse);
    }

    String providerId = userInfoResponse.get("id").getAsString();
    String email = null;
    JsonObject kakaoAccount = userInfoResponse.get("kakao_account").getAsJsonObject();
    if (kakaoAccount != null && !kakaoAccount.isJsonNull()) {
      JsonElement emailElement = kakaoAccount.get("email");
      email = emailElement == null || emailElement.isJsonNull() ? null : emailElement.getAsString();
    }
    return OauthUserInfo.builder()
        .providerId(providerId)
        .email(email)
        .build();
  }
}
