package com.backend.lab.domain.property.sale.entity.dto;

import com.backend.lab.domain.uploadFile.entity.UploadFile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
public class SaleReq {

  private LocalDate saleAt;
  private Long salePrice;
  private Long earningPrice;
  private String contractorName;

  private Long contractId;

  @Setter
  @JsonIgnore
  private UploadFile contract;
}
