package com.backend.lab.common.payment;

import com.backend.lab.domain.purchase.core.entity.vo.PurchaseMethod;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "nice-payment")
public class NicePaymentProperties {

  private String credit_mid;
  private String naver_mid;
  private String kakao_mid;
  private String credit_merchantKey;
  private String naver_merchantKey;
  private String kakao_merchantKey;
  private String credit_callbackUrl;
  private String naver_callbackUrl;
  private String kakao_callbackUrl;

  public String getMid(PurchaseMethod method) {
    return switch (method) {
      case KAKAO -> kakao_mid;
      case NAVER -> naver_mid;
      case CREDIT_CARD -> credit_mid;
    };
  }

  public String getMerchantKey(PurchaseMethod method) {
    return switch (method) {
      case KAKAO -> kakao_merchantKey;
      case NAVER -> naver_merchantKey;
      case CREDIT_CARD -> credit_merchantKey;
    };
  }

  public String getCallbackUrl(PurchaseMethod method) {
    return switch (method) {
      case KAKAO -> kakao_callbackUrl;
      case NAVER -> naver_callbackUrl;
      case CREDIT_CARD -> credit_callbackUrl;
    };
  }
}
