package com.backend.lab.domain.property.pnuTable.entity.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PnuResp {

  private String pnu;
  private String name;
  private String code;
}
