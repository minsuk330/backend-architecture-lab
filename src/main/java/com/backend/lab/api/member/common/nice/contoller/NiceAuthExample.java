package com.backend.lab.api.member.common.nice.contoller;

import com.backend.lab.common.entity.vo.GenderType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/nice-redirectUrl")
@Tag(name = "[회원/공용] 본인인증")
public class NiceAuthExample {

  @Operation(summary = "콜백 redirect 예시")
  @GetMapping
  public void exampleCallback(
      @Parameter(description = "성공여부", required = true) @RequestParam("success") Boolean success,
      @Parameter(description = "결과 메시지", required = true) @RequestParam("message") String message,
      @Parameter(description = "di", required = true) @RequestParam("di") String di,
      @Parameter(description = "이름", required = true) @RequestParam("name") String name,
      @Parameter(description = "전화번호", required = true) @RequestParam("phoneNumber") String phoneNumber,
      @Parameter(description = "생년월일", required = true) @RequestParam("birth") LocalDate birth,
      @Parameter(description = "성별", required = true) @RequestParam("gender") GenderType gender,
      @Parameter(description = "이메일(이메일 회원가입의 경우)", required = false) @RequestParam("email") String email,
      @Parameter(description = "비밀번호(이메일 회원가입의 경우)", required = false) @RequestParam("password") String password

  ) {
    throw new UnsupportedOperationException();
  }
}
