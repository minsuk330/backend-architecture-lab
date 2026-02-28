package com.backend.lab.common.nice.store;

import com.backend.lab.common.auth.session.repository.CustomSessionRepository;
import com.backend.lab.common.nice.NiceAuthFormData;
import com.backend.lab.domain.member.core.entity.vo.MemberType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NiceSessionManager {

  private final CustomSessionRepository customSessionRepository;

  public void register(String email, String password, MemberType memberType,
      NiceAuthFormData data) {
    String sessionId = data.getEncryptInformation().getTokenVersionId();

    JsonObject sessionData = new JsonObject();
    sessionData.addProperty("email", email);
    sessionData.addProperty("password", password);
    sessionData.addProperty("memberType", memberType.toString());

    EncryptInformation encryptInformation = data.getEncryptInformation();
    JsonObject encryptInformationObj = new JsonObject();
    encryptInformationObj.addProperty("key", encryptInformation.getKey());
    encryptInformationObj.addProperty("iv", encryptInformation.getIv());
    encryptInformationObj.addProperty("requestNo", encryptInformation.getRequestNo());
    encryptInformationObj.addProperty("tokenVersionId", encryptInformation.getTokenVersionId());

    sessionData.add("encryptInformation", encryptInformationObj);
    customSessionRepository.put(sessionId, sessionData);
  }

  public EncryptInformation getEncryptInformation(String sessionId) {

    JsonObject obj = (JsonObject) customSessionRepository.get(sessionId);
    JsonObject encryptInformationObj = obj.get("encryptInformation").getAsJsonObject();
    return EncryptInformation.builder()
        .tokenVersionId(encryptInformationObj.get("tokenVersionId").getAsString())
        .key(encryptInformationObj.get("key").getAsString())
        .iv(encryptInformationObj.get("iv").getAsString())
        .requestNo(encryptInformationObj.get("requestNo").getAsString())
        .build();
  }

  public void invalidate(String sessionId) {
    customSessionRepository.remove(sessionId);
  }

  public String getEmail(String sessionId) {
    JsonObject obj = (JsonObject) customSessionRepository.get(sessionId);
    if (obj.has("email")) {
      JsonElement element = obj.get("email");
      if (element.isJsonNull()) {
        return null;
      }
      return element.getAsString();
    }
    return null;
  }

  public String getPassword(String sessionId) {
    JsonObject obj = (JsonObject) customSessionRepository.get(sessionId);
    if (obj.has("password")) {
      JsonElement element = obj.get("password");
      if (element.isJsonNull()) {
        return null;
      }
      return element.getAsString();
    }
    return null;
  }

  public MemberType getMemberType(String sessionId) {
    JsonObject obj = (JsonObject) customSessionRepository.get(sessionId);
    return MemberType.fromName(obj.get("memberType").getAsString());
  }
}
