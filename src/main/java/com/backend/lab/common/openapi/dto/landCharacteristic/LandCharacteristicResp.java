package com.backend.lab.common.openapi.dto.landCharacteristic;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
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
@Schema(description = "토지특성정보 응답")
public class LandCharacteristicResp {
  @Schema(description = "토지면적(㎡)", example = "123.45")
  private Double landArea; // 토지면적
  @Schema(description = "토지면적(평)", example = "123.45")
  private Double pyengLandArea;
  //공시지가(원/㎡)
  @Schema(description = "공시지가m2(원)", example = "5000000")
  private Long officialLandPrice; // 공시지가

  @Schema(description = "동+지번")
  private String dongJibun;

  //공시지가 평당
  @Schema(description = "공시지가평당(원)", example = "5000000")
  private Long pyengLandPrice;
  //공시지가 총합
  @Schema(description = "공시지가총합(원)", example = "5000000")
  private BigDecimal totalLandPrice;

  @Schema(description = "토지이용상황", example = "주택")
  private String landUseStatus; // 토지이용상황

  @Schema(description = "지형높이", example = "평지")
  private String terrainHeight; // 지형높이

  @Schema(description = "지형형상", example = "정형")
  private String terrainShape; // 지형형상

  @Schema(description = "도로접면", example = "일면")
  private String roadContact; // 도로접면

  @Schema(description = "지목명", example = "대지")
  private String landCategoryName; // 지목명

  @Schema(description = "용도지역명1", example = "제1종일반주거지역")
  private String landUseZoneName1; // 용도지역명1

  @Schema(description = "용도지역명2", example = "")
  private String landUseZoneName2; // 용도지역명2


}
