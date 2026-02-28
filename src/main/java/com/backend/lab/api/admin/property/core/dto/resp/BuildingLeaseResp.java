package com.backend.lab.api.admin.property.core.dto.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BuildingLeaseResp {

  @Schema(description = "빌딩 순서")
  private Long buildingOrder;

  private Boolean isPublic;

  @Schema(description = "임대차정보 목록")
  private List<LeaseResp> leaseDetails;

}
