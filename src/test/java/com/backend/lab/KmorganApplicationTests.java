package com.backend.lab;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.logging.log4j.util.Base64Util;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class KmorganApplicationTests {

  @Test
  void test() {
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    System.out.println(
        "passwordEncoder.encode(\"password\") = " + passwordEncoder.encode("password"));
  }
}
