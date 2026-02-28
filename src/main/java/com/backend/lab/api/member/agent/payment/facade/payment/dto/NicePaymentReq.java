package com.backend.lab.api.member.agent.payment.facade.payment.dto;

import com.backend.lab.domain.purchase.core.entity.vo.PurchaseMethod;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NicePaymentReq {
  private String redirectUrl;
  private List<Long> productIds;
  private PurchaseMethod method;
  private String accessToken;
}
