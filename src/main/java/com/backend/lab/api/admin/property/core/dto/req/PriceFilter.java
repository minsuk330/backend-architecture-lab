package com.backend.lab.api.admin.property.core.dto.req;

public interface PriceFilter {
  // 매매가
  Long getMinPrice();
  Long getMaxPrice();

  // 평단가
  Integer getMinPyengPrice();
  Integer getMaxPyengPrice();

  // 보증금
  Integer getMinDepositPrice();
  Integer getMaxDepositPrice();

  // 월임대료
  Integer getMinMonthPrice();
  Integer getMaxMonthPrice();

  // 관리비
  Integer getMinGrprice();
  Integer getMaxGrprice();

  // 관리비 지출
  Integer getMinGrout();
  Integer getMaxGrout();

  Double getMinRoi();
  Double getMaxRoi();
}
