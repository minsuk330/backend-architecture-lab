package com.backend.lab.common.payment.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NicePaymentApprovalReq {

  private String AuthResultCode;
  private String AuthResultMsg;
  private String AuthToken;
  private String PayMethod;
  private String MID;
  private String Moid;
  private String Signature;
  private Long Amt;
  private String TxTid;
  private String NextAppURL;
  private String NetCancelURL;
  private String ReqReserved;
}
