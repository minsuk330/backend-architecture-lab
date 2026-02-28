package com.backend.lab.common.util;

public class StringUtil {

  public static String generateRandomString(int length) {
    StringBuilder sb = new StringBuilder();
    String specialCharacters = "!@#$%^&*";
    String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    int minimumSpecialChars = 2;

    for (int i = 0; i < minimumSpecialChars; i++) {
      int randomIndex = (int) (Math.random() * specialCharacters.length());
      sb.append(specialCharacters.charAt(randomIndex));
    }

    for (int i = minimumSpecialChars; i < length; i++) {
      int randomIndex = (int) (Math.random() * characters.length());
      sb.append(characters.charAt(randomIndex));
    }

    for (int i = 0; i < sb.length(); i++) {
      int randomIndex = (int) (Math.random() * sb.length());
      char temp = sb.charAt(i);
      sb.setCharAt(i, sb.charAt(randomIndex));
      sb.setCharAt(randomIndex, temp);
    }

    return sb.toString();
  }
}
