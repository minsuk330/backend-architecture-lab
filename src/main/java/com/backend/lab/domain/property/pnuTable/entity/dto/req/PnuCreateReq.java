package com.backend.lab.domain.property.pnuTable.entity.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PnuCreateReq {

  private String pnu;
  private String sido;
  private String sigungu;
  private String bjd;
}
