package com.backend.lab.common.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtil {

  private static String encryptSHA256(String data) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
      StringBuilder hexString = new StringBuilder();
      for (byte b : hash) {
        String hex = Integer.toHexString(0xff & b);
        if (hex.length() == 1) {
          hexString.append('0');
        }
        hexString.append(hex);
      }
      return hexString.toString();
    } catch (Exception e) {
      throw new RuntimeException("Failed to encrypt SHA256", e);
    }
  }

  private static byte[] hmac256(byte[] data, byte[] key) {
    try {
      Mac mac = Mac.getInstance("HmacSHA256");
      SecretKeySpec secretKeySpec = new SecretKeySpec(data, "HmacSHA256");
      mac.init(secretKeySpec);
      return mac.doFinal(key);
    } catch (Exception e) {
      throw new RuntimeException("Failed to generate HMAC", e);
    }
  }
}
