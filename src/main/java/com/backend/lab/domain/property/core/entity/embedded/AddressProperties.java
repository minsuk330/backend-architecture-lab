package com.backend.lab.domain.property.core.entity.embedded;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@Builder
@Schema(description = "주소 정보")
public class AddressProperties {

  @Schema(description = "필지고유번호")
  private String pnu; // 필지고유번호
  private String roadAddress;
  private String jibunAddress;

  @Schema(description = "위도")
  private Double lat; // 위도
  @Schema(description = "경도")
  private Double lng; // 경도

  @Schema(description = "번 코드")
  private Integer bunCode; // 번 코드
  @Schema(description = "지 코드")
  private Integer jiCode; // 지 코드
}
