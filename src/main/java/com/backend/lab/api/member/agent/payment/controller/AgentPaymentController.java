package com.backend.lab.api.member.agent.payment.controller;

import com.backend.lab.api.member.agent.payment.facade.payment.AgentPaymentFacade;
import com.backend.lab.api.member.agent.payment.facade.payment.dto.NicePaymentReq;
import com.backend.lab.api.member.agent.payment.facade.payment.dto.PaymentResp;
import com.backend.lab.common.payment.dto.NicePaymentApprovalReq;
import com.backend.lab.common.payment.dto.NicePaymentData;
import com.backend.lab.domain.purchase.core.entity.vo.PurchaseMethod;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@RequestMapping("/agent/payment")
@RequiredArgsConstructor
@Tag(name = "[회원/공인중개사] 아이템 결제")
@Slf4j
public class AgentPaymentController {

  private final AgentPaymentFacade agentPaymentFacade;

  @Operation(summary = "결제 페이지로 이동")
  @GetMapping("/checkout")
  public String checkout(
      @ModelAttribute NicePaymentReq req,
      Model model
  ) {
    NicePaymentData data = agentPaymentFacade.getFormData(req);
    model.addAttribute("data", data);
    return "checkout";
  }

  @PostMapping("/callback/{method}")
  public RedirectView callback(
      @ModelAttribute NicePaymentApprovalReq req,
      @PathVariable("method") String methodName
  ) {
    PurchaseMethod purchaseMethod = PurchaseMethod.valueOf(methodName);
    PaymentResp resp = agentPaymentFacade.approve(req, purchaseMethod);
    return this.makeRedirectView(resp);
  }

  private RedirectView makeRedirectView(PaymentResp resp) {
    RedirectView redirectView = new RedirectView();
    String redirectUrl = resp.getRedirectUrl();
    if (!redirectUrl.contains("/")) {
      redirectUrl = URLDecoder.decode(redirectUrl, StandardCharsets.UTF_8);
    }
    String nextUri = UriComponentsBuilder.fromUri(URI.create(redirectUrl))
        .queryParam("success", resp.isSuccess())
        .queryParam("message", URLEncoder.encode(resp.getMessage(), StandardCharsets.UTF_8))
        .toUriString();
    redirectView.setUrl(nextUri);
    return redirectView;
  }

}
