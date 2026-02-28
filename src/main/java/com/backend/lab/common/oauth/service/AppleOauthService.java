package com.backend.lab.common.oauth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.backend.lab.common.oauth.dto.OauthUserInfo;
import com.backend.lab.common.oauth.properties.AppleOauthProperties;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class AppleOauthService implements OauthService{

  private final AppleOauthProperties appleOauthProperties;
  private final WebClient webClient;

  @Override
  public URI getLoginURI(String redirectUri) {
    return UriComponentsBuilder.fromUri(URI.create(appleOauthProperties.getAuthorizationUrl()))
        .queryParam("client_id",appleOauthProperties.getClientId())
        .queryParam("redirect_uri",appleOauthProperties.getCallbackUrl())
        .queryParam("response_type","code id_token")
        .queryParam("scope","name email")
        .queryParam("response_mode","form_post")
        .queryParam("state", URLEncoder.encode(redirectUri, StandardCharsets.UTF_8))
        .build()
        .toUri();
  }

  @Override
  public OauthUserInfo getProviderId(String payload, String state) {
    try {
      String clientSecret = createClientSecret();
      String code = payload;

      final String baseUrl = "https://appleid.apple.com/auth/token";
      String contentType = "application/x-www-form-urlencoded;charset=utf-8";

      MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
      formData.add("grant_type", "authorization_code");
      formData.add("client_id", appleOauthProperties.getClientId());
      formData.add("client_secret", clientSecret);
      formData.add("code", code);
      formData.add("redirect_uri", appleOauthProperties.getCallbackUrl());

      String responseRaw = webClient.post()
          .uri(baseUrl)
          .header("Content-Type", contentType)
          .bodyValue(formData)
          .retrieve()
          .bodyToMono(String.class)
          .block();

      JsonObject response = JsonParser.parseString(responseRaw).getAsJsonObject();
      if (response == null || response.isJsonNull()) {
        throw new RuntimeException("Invalid response from Apple OAuth");
      }

      if (!response.has("id_token")) {
        throw new RuntimeException("Invalid response from Apple OAuth: " + response);
      }

      String idToken = response.get("id_token").getAsString();

      String[] tokenParts = idToken.split("\\.");
      if (tokenParts.length != 3) {
        throw new RuntimeException("Invalid JWT token format");
      }

      String payload64 = tokenParts[1];
      byte[] decodedBytes = Base64.getUrlDecoder().decode(payload64);
      String payloadJson = new String(decodedBytes, StandardCharsets.UTF_8);

      JsonObject payloadObject = JsonParser.parseString(payloadJson).getAsJsonObject();
      if (payloadObject == null || payloadObject.isJsonNull()) {
        throw new RuntimeException("Invalid JWT payload");
      }

      if (!payloadObject.has("sub")) {
        throw new RuntimeException("Invalid JWT payload: missing sub claim");
      }

      String providerId = payloadObject.get("sub").getAsString();
      String email = null;

      // 이메일 추출 (존재할 경우)
      if (payloadObject.has("email")) {
        JsonElement emailElement = payloadObject.get("email");
        email = emailElement == null || emailElement.isJsonNull() ? null :  emailElement.getAsString();
      }

      return OauthUserInfo.builder()
          .providerId(providerId)
          .email(email)
          .build();

    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to get Apple provider ID: " + e.getMessage());
    }
  }

  private String createClientSecret() throws Exception {
    try {
      byte[] keyBytes = getPrivateKey();
      PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
      KeyFactory keyFactory = KeyFactory.getInstance("EC");
      ECPrivateKey privateKey = (ECPrivateKey) keyFactory.generatePrivate(spec);

      Algorithm algorithm = Algorithm.ECDSA256(null, privateKey);

      Date now = new Date();
      Date expiryTime = new Date(now.getTime() + 3600000);

      final String authUrl = "https://appleid.apple.com";

      return JWT.create()
          .withKeyId(appleOauthProperties.getKeyId())
          .withIssuer(appleOauthProperties.getTeamId())
          .withIssuedAt(now)
          .withExpiresAt(expiryTime)
          .withAudience(authUrl)
          .withSubject(appleOauthProperties.getClientId())
          .sign(algorithm);

    } catch (Exception e) {
      throw new Exception("Failed create client secret");
    }
  }

  private byte[] getPrivateKey() throws Exception {
    byte[] content = null;
    File file = null;

    URL res = getClass().getResource(appleOauthProperties.getKeyPath());

    if (res == null) {
      throw new RuntimeException("not found apple pem");
    }

    if ("jar".equals(res.getProtocol())) {
      try {
        InputStream input = getClass().getResourceAsStream(appleOauthProperties.getKeyPath());
        file = File.createTempFile("tempfile", ".tmp");
        OutputStream out = new FileOutputStream(file);

        int read;
        byte[] bytes = new byte[1024];

        while ((read = input.read(bytes)) != -1) {
          out.write(bytes, 0, read);
        }

        out.close();
        file.deleteOnExit();
      } catch (IOException ignored) {
      }
    } else {
      file = new File(res.getFile());
    }

    if (file == null) {
      throw new RuntimeException("not found apple pem");
    }

    if (file.exists()) {
      try (FileReader keyReader = new FileReader(file);
          PemReader pemReader = new PemReader(keyReader))
      {
        PemObject pemObject = pemReader.readPemObject();
        content = pemObject.getContent();
      } catch (IOException ignored) {
      }
    } else {
      throw new RuntimeException("File " + file + " not found");
    }

    return content;
  }
}
