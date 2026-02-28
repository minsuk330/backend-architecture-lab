package com.backend.lab.api.member.agent.payment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/payment-redirectUrl")
@Tag(name = "[회원/공인중개사] 아이템 결제")
public class AgentPaymentExample {

  @Operation(summary = "콜백 redirect 예시")
  @GetMapping
  public void exampleCallback(
      @Parameter(description = "성공여부", required = true) @RequestParam("success") Boolean success,
      @Parameter(description = "결과 메시지", required = true) @RequestParam("message") String message
  ) {
    throw new UnsupportedOperationException();
  }
}
