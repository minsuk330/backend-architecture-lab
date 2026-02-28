package com.backend.lab.common.openapi.dto.landUsePlan;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
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
@Schema(description = "토지이용계획정보 응답")
public class LandUseResp {

  @Builder
  @Setter
  @Getter
  public static class Item {
    @Schema(description = "용도지역지구명")
    private String name;

    @Schema(description = "저촉여부(접함·저촉·포함)")
    private String constructableStatus;
  }


    @Schema(description = "국토의 계획 및 이용에 관한 법률 항목 목록")
    private List<Item> nationalLawItems;

    @Schema(description = "기타 법률 항목 목록")
    private List<Item> otherLawItems;


}
