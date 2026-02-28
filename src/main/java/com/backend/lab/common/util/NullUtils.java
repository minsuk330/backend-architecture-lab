package com.backend.lab.common.util;

public class NullUtils {

  public static boolean getBoolean(Boolean b) {
    if (b == null) {
      return false;
    }
    return b;
  }
}
