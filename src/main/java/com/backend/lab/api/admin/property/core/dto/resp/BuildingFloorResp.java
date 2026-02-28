package com.backend.lab.api.admin.property.core.dto.resp;

import com.backend.lab.common.openapi.dto.floor.FloorResp;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BuildingFloorResp {

  @Schema(description = "빌딩 순서")
  private Long buildingOrder;
  @Schema(description = "층별 정보 목록")
  private List<FloorResp> floors;

}
