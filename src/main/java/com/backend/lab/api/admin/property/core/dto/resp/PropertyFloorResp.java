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
public class PropertyFloorResp {

  @Schema(description = "건물 층정보 목록")
  private List<BuildingFloorResp> buildingFloors;

  @Schema(description = "임대차 정보")
  private List<BuildingLeaseResp> leaseStatus;

//  @Schema(description = "임대차 현황 요약")
//  private LeaseStatusSummary summary;


}
