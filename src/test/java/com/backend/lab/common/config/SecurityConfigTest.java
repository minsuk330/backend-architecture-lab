package com.backend.lab.common.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

class SecurityConfigTest {


  @Test
  void test() {
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    System.out.println(
        "passwordEncoder.encode(\"password\") = " + passwordEncoder.encode("password"));
  }
}