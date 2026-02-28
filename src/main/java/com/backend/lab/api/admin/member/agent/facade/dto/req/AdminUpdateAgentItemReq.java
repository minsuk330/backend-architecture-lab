package com.backend.lab.api.admin.member.agent.facade.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminUpdateAgentItemReq {

  @Schema(description = "연락처 열람권 개수", example = "10")
  @Min(value = 0, message = "연락처 열람권 개수는 0 이상이어야 합니다")
  private Integer showContactCount;

  @Schema(description = "매물 광고권 개수", example = "5")
  @Min(value = 0, message = "매물 광고권 개수는 0 이상이어야 합니다")
  private Integer advertiseCount;
}
