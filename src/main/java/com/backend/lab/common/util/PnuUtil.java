package com.backend.lab.common.util;

public class PnuUtil {

  public static String getSidoName(String code) {

    if (code == null || code.isEmpty()) {
      return null;
    }

    return "mock-sidoName";
  }

  public static String getSigunguName(String code) {

    if (code == null || code.isEmpty()) {
      return null;
    }

    return "mock-sigunguName";
  }

  public static String getBjdongName(String code) {

    if (code == null || code.isEmpty()) {
      return null;
    }

    return "mock-bjdongName";
  }
}
