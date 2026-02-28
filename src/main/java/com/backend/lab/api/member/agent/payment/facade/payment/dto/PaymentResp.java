package com.backend.lab.api.member.agent.payment.facade.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResp {

  private boolean success;
  private String message;
  private String redirectUrl;
}
