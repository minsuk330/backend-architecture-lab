package com.backend.lab.common.openapi.dto.landPossession;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "토지소유정보 응답")
public class LandPossessionResp {

  @Schema(description = "소유권 변동일자")
  private String ownshipChgDe;
  @Schema(description = "소유구분")
  private String posesnSeCodeNm;
  @Schema(description = "소유권 변동 원인")
  private String ownshipChgCauseCodeNm;

}
