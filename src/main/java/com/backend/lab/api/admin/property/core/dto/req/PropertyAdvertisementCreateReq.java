package com.backend.lab.api.admin.property.core.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PropertyAdvertisementCreateReq {

  @Schema(description = "매물 ID", example = "1")
  @NotNull(message = "매물 ID는 필수입니다")
  private Long propertyId;

  @Schema(description = "공인중개사 ID", example = "1")
  @NotNull(message = "공인중개사 ID는 필수입니다")
  private Long agentId;

  @Schema(description = "광고 시작일", example = "2025-10-12T00:00:00")
  @NotNull(message = "광고 시작일은 필수입니다")
  private LocalDateTime startDate;

  @Schema(description = "광고 종료일", example = "2025-11-12T23:59:59")
  @NotNull(message = "광고 종료일은 필수입니다")
  private LocalDateTime endDate;
}
