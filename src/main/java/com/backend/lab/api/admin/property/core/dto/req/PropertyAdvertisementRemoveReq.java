package com.backend.lab.api.admin.property.core.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PropertyAdvertisementRemoveReq {

  @Schema(description = "매물 ID", example = "1")
  @NotNull(message = "매물 ID는 필수입니다")
  private Long propertyId;

  @Schema(description = "공인중개사 ID", example = "1")
  @NotNull(message = "공인중개사 ID는 필수입니다")
  private Long agentId;
}
