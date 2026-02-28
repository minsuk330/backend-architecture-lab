package com.backend.lab.common.payment;

import com.backend.lab.api.member.agent.payment.facade.payment.dto.PaymentResp;
import com.backend.lab.common.payment.dto.NicePaymentApprovalReq;
import com.backend.lab.common.payment.dto.NicePaymentData;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.entity.embedded.AgentProperties;
import com.backend.lab.domain.purchase.core.entity.vo.PurchaseMethod;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class NicePaymentService {

  private final NicePaymentProperties nicePaymentProperties;
  private final WebClient webClient;

  public NicePaymentData getFormData(String goodsName, Integer amount, String moid,
      PurchaseMethod method, Member member, String redirectUrl) {

    String ediDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    String signData = DigestUtils.sha256Hex(
        ediDate + nicePaymentProperties.getMid(method) + String.valueOf(amount)
            + nicePaymentProperties.getMerchantKey(method)
    );

    AgentProperties agentProperties = member.getAgentProperties();
    String buyerName = agentProperties.getName();
    String buyerEmail = member.getEmail();

    String directShowOpt = null;
    String directEasyPay = null;
    String nicepayReserved = null;
    String easyPayMethod = null;

    switch (method) {
      case KAKAO:
        directShowOpt = "CARD";
        nicepayReserved = "DirectKakao=Y";
        break;
      case NAVER:
        directShowOpt = "CARD";
        directEasyPay = "E020";
        easyPayMethod = "E020=POINT";
        break;
      case CREDIT_CARD:
        break;
    }

    return NicePaymentData.builder()
        .actionUrl(nicePaymentProperties.getCallbackUrl(method))
        .goodsName(goodsName)
        .amt(amount)
        .mid(nicePaymentProperties.getMid(method))
        .moid(moid)
        .buyerName(buyerName)
        .buyerEmail(buyerEmail)
        .returnUrl(nicepayReserved)
        .reqReserved(redirectUrl)
        .ediDate(ediDate)
        .signData(signData)
        .directShowOpt(directShowOpt)
        .directEasyPay(directEasyPay)
        .easyPayMethod(easyPayMethod)
        .nicepayReserved(nicepayReserved)
        .build();
  }

  public PaymentResp approve(NicePaymentApprovalReq req, PurchaseMethod purchaseMethod) {
    String baseUrl = req.getNextAppURL();

    String ediDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    String signData = DigestUtils.sha256Hex(
        req.getAuthToken() +
            nicePaymentProperties.getMid(purchaseMethod) +
            req.getAmt() +
            ediDate +
            nicePaymentProperties.getMerchantKey(purchaseMethod)
    );

    String rawResponse = webClient.post()
        .uri(baseUrl)
        .header("Content-Type", "application/x-www-form-urlencoded")
        .body(BodyInserters
            .fromFormData("TID", req.getTxTid())
            .with("AuthToken", req.getAuthToken())
            .with("MID", nicePaymentProperties.getMid(purchaseMethod))
            .with("Amt", req.getAmt().toString())
            .with("EdiDate", ediDate)
            .with("SignData", signData)
            .with("CharSet", "utf-8")
            .with("EdiType", "JSON")
        )
        .retrieve()
        .bodyToMono(String.class)
        .block();

    JsonObject jsonResponse = JsonParser.parseString(rawResponse).getAsJsonObject();

    if (!"3001".equals(jsonResponse.get("ResultCode").getAsString())) {
      return PaymentResp.builder()
          .success(false)
          .message(jsonResponse.get("ResultMsg").getAsString())
          .build();
    }

    return PaymentResp.builder()
        .success(true)
        .message("결제 성공")
        .build();
  }
}
