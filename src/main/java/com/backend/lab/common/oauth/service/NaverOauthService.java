package com.backend.lab.common.oauth.service;

import com.backend.lab.common.oauth.dto.OauthUserInfo;
import com.backend.lab.common.oauth.properties.NaverOauthProperties;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class NaverOauthService implements OauthService {

  private final NaverOauthProperties naverOauthProperties;
  private final WebClient webClient;

  @Override
  public URI getLoginURI(String redirectUri) {
    return UriComponentsBuilder.fromUri(URI.create(naverOauthProperties.getAuthorizationUrl()))
        .queryParam("client_id", naverOauthProperties.getClientId())
        .queryParam("response_type", "code")
        .queryParam("redirect_uri", naverOauthProperties.getCallbackUrl())
        .queryParam("state", URLEncoder.encode(redirectUri, StandardCharsets.UTF_8))
        .build()
        .toUri();
  }

  @Override
  public OauthUserInfo getProviderId(String payload, String state) {

    final String baseUrl = "https://nid.naver.com/oauth2.0/token";

    String grantType = "authorization_code";
    String clientId = naverOauthProperties.getClientId();
    String clientSecret = naverOauthProperties.getClientSecret();
    String code = payload;

    UriComponents validateUri = UriComponentsBuilder.fromUri(URI.create(baseUrl))
        .queryParam("grant_type", grantType)
        .queryParam("client_id", clientId)
        .queryParam("client_secret", clientSecret)
        .queryParam("code", code)
        .queryParam("state", state)
        .build();

    String responseRaw = webClient.get()
        .uri(validateUri.toUri())
        .header("X-Naver-Client-Id", naverOauthProperties.getClientId())
        .header("X-Naver-Client-Secret", naverOauthProperties.getClientSecret())
        .retrieve()
        .bodyToMono(String.class)
        .block();
    JsonObject response = JsonParser.parseString(responseRaw).getAsJsonObject();
    if (response == null || response.isJsonNull()) {
      throw new RuntimeException("Invalid response from Naver OAuth");
    }

    if (response.has("error")) {
      throw new RuntimeException(response.get("error_description").getAsString());
    }

    String accessToken = response.get("access_token").getAsString();

    final String getProfileBaseUrl = "https://openapi.naver.com/v1/nid/me";

    String profileResponseRaw = webClient.get()
        .uri(getProfileBaseUrl)
        .header("Authorization", "Bearer " + accessToken)
        .retrieve()
        .bodyToMono(String.class)
        .block();

    if (profileResponseRaw == null || profileResponseRaw.isEmpty()) {
      throw new RuntimeException("Failed to retrieve profile from Naver OAuth");
    }

    JsonObject profileResponse = JsonParser.parseString(profileResponseRaw).getAsJsonObject();
    String resultCode = profileResponse.get("resultcode").getAsString();
    String resultMessage = profileResponse.get("message").getAsString();

    if (!"00".equals(resultCode)) {
      throw new RuntimeException("Failed to retrieve profile from Naver OAuth: " + resultMessage);
    }

    String providerId = profileResponse.get("response").getAsJsonObject().get("id").getAsString();
    JsonElement emailElement = profileResponse.get("response").getAsJsonObject().get("email");
    String email = emailElement == null || emailElement.isJsonNull() ? null : emailElement.getAsString();
    return OauthUserInfo.builder()
        .providerId(providerId)
        .email(email)
        .build();
  }
}
