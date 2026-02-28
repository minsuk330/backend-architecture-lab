package com.backend.lab.common.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NicePaymentData {

  private String actionUrl;
  private String goodsName;
  private Integer amt;
  private String mid;
  private String moid;
  private String buyerName;
  private String buyerEmail;
  private String returnUrl;
  private String reqReserved;
  private String ediDate;
  private String signData;

  private String directShowOpt;
  private String directEasyPay;
  private String easyPayMethod;
  private String nicepayReserved;
}
