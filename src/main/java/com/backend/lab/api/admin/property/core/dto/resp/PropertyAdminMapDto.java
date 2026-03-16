package com.backend.lab.api.admin.property.core.dto.resp;

import com.backend.lab.domain.property.core.entity.vo.PropertyStatus;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PropertyAdminMapDto {

  private Long id;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private Long mmPrice;
  private Long pyeongPrice;
  private PropertyStatus status;
  private String buildingName;
  private String thumbnailFileName;
  private String thumbnailFileUrl;
  private String pnu;
  private String roadAddress;
  private String jibunAddress;
  private Double lat;
  private Double lng;
  private Integer minFloor;
  private Integer maxFloor;
  private Double totalYeonAreaPyeng;
  private Double totalYeonAreaMeter;
  private Double totalBuildingAreaPyeng;
  private Double totalBuildingAreaMeter;
  private Double totalLandAreaPyeong;
  private Double totalLandAreaMeter;

  @QueryProjection
  public PropertyAdminMapDto(
      Long id,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      Long mmPrice,
      Long pyeongPrice,
      PropertyStatus status,
      String buildingName,
      String thumbnailFileName,
      String thumbnailFileUrl,
      String pnu,
      String roadAddress,
      String jibunAddress,
      Double lat,
      Double lng,
      Integer minFloor,
      Integer maxFloor,
      Double totalYeonAreaPyeng,
      Double totalYeonAreaMeter,
      Double totalBuildingAreaPyeng,
      Double totalBuildingAreaMeter,
      Double totalLandAreaPyeong,
      Double totalLandAreaMeter) {
    this.id = id;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.mmPrice = mmPrice;
    this.pyeongPrice = pyeongPrice;
    this.status = status;
    this.buildingName = buildingName;
    this.thumbnailFileName = thumbnailFileName;
    this.thumbnailFileUrl = thumbnailFileUrl;
    this.pnu = pnu;
    this.roadAddress = roadAddress;
    this.jibunAddress = jibunAddress;
    this.lat = lat;
    this.lng = lng;
    this.minFloor = minFloor;
    this.maxFloor = maxFloor;
    this.totalYeonAreaPyeng = totalYeonAreaPyeng;
    this.totalYeonAreaMeter = totalYeonAreaMeter;
    this.totalBuildingAreaPyeng = totalBuildingAreaPyeng;
    this.totalBuildingAreaMeter = totalBuildingAreaMeter;
    this.totalLandAreaPyeong = totalLandAreaPyeong;
    this.totalLandAreaMeter = totalLandAreaMeter;
  }
}
