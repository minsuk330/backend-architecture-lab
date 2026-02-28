package com.backend.lab.common.nice;

import com.backend.lab.common.entity.vo.GenderType;
import com.backend.lab.common.nice.dto.NiceAuthResp;
import com.backend.lab.common.nice.store.EncryptInformation;
import com.backend.lab.domain.member.core.entity.vo.MemberType;
import com.backend.lab.domain.member.core.service.MemberService;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.UUID;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class NiceAuthService {

  private final NiceProperties niceProperties;
  private final WebClient webClient;
  private final MemberService memberService;

  public NiceAuthFormData getEncFormData(String redirectUrl) {
    try {
      String requestNo = generateReqNo();
      String reqDtim = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
      JsonObject encryptTokenResp = issueEncryptToken(requestNo, reqDtim);

      String siteCode = encryptTokenResp.get("site_code").getAsString();
      String tokenVal = encryptTokenResp.get("token_val").getAsString();
      String tokenVersionId = encryptTokenResp.get("token_version_id").getAsString();

      String key = this.getSymmetricKey(reqDtim, requestNo, tokenVal);
      String iv = this.getSymmetricIv(reqDtim, requestNo, tokenVal);
      String hmacKey = this.getHmacKey(reqDtim, requestNo, tokenVal);

      JsonObject requestData = new JsonObject();
      requestData.addProperty("requestno", requestNo);
      requestData.addProperty("returnurl", niceProperties.getCallbackUrl());
      requestData.addProperty("sitecode", siteCode);
      requestData.addProperty("methodtype", "get");
      requestData.addProperty("popupyn", "N");
      requestData.addProperty("receivedata", redirectUrl);

      String encData = encrypt(requestData, key, iv);
      String integrityValue = hmac256(encData, hmacKey);

      return NiceAuthFormData.builder()
          .tokenVersionId(tokenVersionId)
          .encData(encData)
          .integrityValue(integrityValue)
          .encryptInformation(
              EncryptInformation.builder()
                  .tokenVersionId(tokenVersionId)
                  .requestNo(requestNo)
                  .key(key)
                  .iv(iv)
                  .build()
          )
          .build();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public NiceAuthResp handleCallback(
      String tokenVersionId,
      String encData,
      String integrityValue,
      EncryptInformation encryptInformation,
      String email, String password, MemberType memberType) {
    String returnUrl = null;
    try {

      if (!encryptInformation.getTokenVersionId().equals(tokenVersionId)) {
        throw new IllegalArgumentException("Token version ID does not match");
      }

      String key = encryptInformation.getKey();
      String iv = encryptInformation.getIv();

      SecretKey secureKey = new SecretKeySpec(key.getBytes(), "AES");
      Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
      c.init(Cipher.DECRYPT_MODE, secureKey, new IvParameterSpec(iv.getBytes()));
      byte[] cipherEnc = Base64.getDecoder().decode(encData);
      String resData = new String(c.doFinal(cipherEnc), "euc-kr");
      JsonObject responseData = JsonParser.parseString(resData).getAsJsonObject();

      if (!responseData.has("receivedata")) {
        throw new IllegalStateException("리다이렉트 URL이 응답에 포함되어 있지 않습니다");
      }

      returnUrl = responseData.get("receivedata").getAsString();

      if (!responseData.has("resultcode") || !"0000".equals(
          responseData.get("resultcode").getAsString())) {
        throw new IllegalStateException("인증에 실패하였습니다");
      }

      if (!responseData.has("requestno") || !encryptInformation.getRequestNo()
          .equals(responseData.get("requestno").getAsString())) {
        throw new IllegalStateException("요청 번호가 일치하지 않습니다");
      }

      if (!responseData.has("mobileno") || !responseData.has("di")) {
        throw new IllegalStateException("전화번호가 응답에 포함되어 있지 않습니다");
      }

      String phoneNumber = responseData.get("mobileno").getAsString();
      String di = responseData.get("di").getAsString();


      if (memberService.existByTypeAndPhoneNumber(phoneNumber, memberType)) {
        return NiceAuthResp.builder()
            .success(false)
            .message("이미 가입된 회원입니다.")
            .returnUrl(returnUrl)
            .build();
      }

      String name = null;
      LocalDate birth = null;
      GenderType gender = null;

      if (responseData.has("name")) {
        name = responseData.get("name").getAsString();
      }

      if (responseData.has("birthdate")) {
        String birthStr = responseData.get("birthdate").getAsString();
        birth = LocalDate.parse(birthStr, DateTimeFormatter.ofPattern("yyyyMMdd"));
      }

      if (responseData.has("gender")) {
        String genderCode = responseData.get("gender").getAsString();
        if ("0".equals(genderCode)) {
          gender = GenderType.FEMALE;
        } else if ("1".equals(genderCode)) {
          gender = GenderType.MALE;
        }
      }

      return NiceAuthResp.builder()
          .success(true)
          .message("success")
          .di(di)
          .name(name)
          .phoneNumber(phoneNumber)
          .birth(birth)
          .gender(gender)

          .email(email)
          .password(password)

          .returnUrl(returnUrl)
          .build();
    } catch (Exception e) {
      return NiceAuthResp.builder()
          .success(false)
          .returnUrl(returnUrl)
          .message(e.getMessage())
          .build();
    }
  }

  private String encrypt(JsonObject requestData, String key, String iv)
      throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
    SecretKey secureKey = new SecretKeySpec(key.getBytes(), "AES");
    Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
    c.init(Cipher.ENCRYPT_MODE, secureKey, new IvParameterSpec(iv.getBytes()));
    byte[] encrypted = c.doFinal(requestData.toString().trim().getBytes());
    return Base64.getEncoder().encodeToString(encrypted);
  }

  private String hmac256(String data, String key)
      throws NoSuchAlgorithmException, InvalidKeyException {
    SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA256");
    Mac mac = Mac.getInstance("HmacSHA256");
    SecretKeySpec sks = new SecretKeySpec(key.getBytes(), "HmacSHA256");
    mac.init(sks);
    byte[] hmac = mac.doFinal(data.getBytes());
    return Base64.getEncoder().encodeToString(hmac);
  }

  private String getSymmetricKey(String reqDtim, String reqNo, String tokenVal)
      throws NoSuchAlgorithmException {
    String value = reqDtim.trim() + reqNo.trim() + tokenVal.trim();
    MessageDigest md = MessageDigest.getInstance("SHA-256");
    md.update(value.getBytes());
    byte[] digest = md.digest();
    String encoded = Base64.getEncoder().encodeToString(digest);
    return encoded.substring(0, 16);
  }

  private String getSymmetricIv(String reqDtim, String reqNo, String tokenVal)
      throws NoSuchAlgorithmException {
    String value = reqDtim.trim() + reqNo.trim() + tokenVal.trim();
    MessageDigest md = MessageDigest.getInstance("SHA-256");
    md.update(value.getBytes());
    byte[] digest = md.digest();
    String encoded = Base64.getEncoder().encodeToString(digest);
    return encoded.substring(encoded.length() - 16);
  }

  private String getHmacKey(String reqDtim, String reqNo, String tokenVal)
      throws NoSuchAlgorithmException {
    String value = reqDtim.trim() + reqNo.trim() + tokenVal.trim();
    MessageDigest md = MessageDigest.getInstance("SHA-256");
    md.update(value.getBytes());
    byte[] digest = md.digest();
    String encoded = Base64.getEncoder().encodeToString(digest);
    return encoded.substring(0, 32);
  }

  private String generateReqNo() {
    return UUID.randomUUID().toString().replace("-", "").substring(0, 30);
  }

  private JsonObject issueEncryptToken(String reqNo, String reqDtim) {
    final String baseUrl = "https://svc.niceapi.co.kr:22001/digital/niceid/api/v1.0/common/crypto/token";
    final String CNTY_CD = "ko";

    JsonObject dataHeader = new JsonObject();
    dataHeader.addProperty("CNTY_CD", CNTY_CD);

    JsonObject dataBody = new JsonObject();
    String encMode = "1";

    dataBody.addProperty("req_dtim", reqDtim);
    dataBody.addProperty("req_no", reqNo);
    dataBody.addProperty("enc_mode", encMode);

    JsonObject requestBody = new JsonObject();
    requestBody.add("dataHeader", dataHeader);
    requestBody.add("dataBody", dataBody);

    long systemSec = System.currentTimeMillis() / 1000;
    String authTokenRaw =
        niceProperties.getAccessToken() + ":" + systemSec + ":" + niceProperties.getClientId();
    String authToken = Base64.getEncoder().encodeToString(authTokenRaw.getBytes());

    String rawResponse = webClient.post()
        .uri(baseUrl)
        .header("Content-Type", "application/json")
        .header("client_id", niceProperties.getClientId())
        .header("productID", niceProperties.getProductId())
        .header("Authorization", "bearer " + authToken)
        .bodyValue(requestBody.toString())
        .retrieve()
        .bodyToMono(String.class)
        .block();

    JsonElement response = JsonParser.parseString(rawResponse);
    if (response == null || !response.isJsonObject()) {
      throw new IllegalStateException("Invalid response from Nice API");
    }

    JsonObject responseObj = response.getAsJsonObject();
    JsonObject headerObject = responseObj.getAsJsonObject("dataHeader");

    if (headerObject != null) {
      final String SUCCESS_CODE = "1200";
      if (SUCCESS_CODE.equals(headerObject.get("GW_RSLT_CD").getAsString())) {
        return responseObj.getAsJsonObject("dataBody");
      }
    } else {
      throw new IllegalStateException(
          "Nice API 암호화 토큰 발행에 실패하였습니다 :" + headerObject.get("GW_RSLT_MSG").getAsString());
    }

    throw new IllegalStateException("Invalid response body from Nice API");
  }
}
