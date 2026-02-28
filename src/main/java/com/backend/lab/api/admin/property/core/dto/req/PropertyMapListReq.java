package com.backend.lab.api.admin.property.core.dto.req;

import com.backend.lab.domain.property.category.entity.vo.MenuType;
import com.backend.lab.domain.property.core.entity.vo.PropertySortType;
import com.backend.lab.domain.property.core.entity.vo.PropertyStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;

@Getter
@ParameterObject
@Setter
public class PropertyMapListReq implements AreaFilter,PriceFilter{


  @Schema(description = "좌상단 위도 (lat)")
  @NotNull
  private Double ltLat;

  @Schema(description = "좌상단 경도 (lng)")
  @NotNull
  private Double ltLng;

  @Schema(description = "우하단 위도 (lat)")
  @NotNull
  private Double rbLat;

  @Schema(description = "우하단 경도 (lng)")
  @NotNull
  private Double rbLng;

  private PropertySortType sortType;
  private List<MenuType> menuTypes;

  // 진행상태 필터
  private PropertyStatus status ;

  private String sidoCode;
  private String sigungnCode;
  private String bjdCode;

  // 수익률 범위
  private Double minRoi;
  private Double maxRoi;

  private Boolean isPublic;

  // 금액 필터들
  private Long minPrice;      // 매매가
  private Long maxPrice;
  private Integer minPyengPrice;   // 평단가
  private Integer maxPyengPrice;
  private Integer minDepositPrice;  // 보증금
  private Integer maxDepositPrice;
  private Integer minMonthPrice; // 월임대료
  private Integer maxMonthPrice;
  private Integer minGrprice; //관리비
  private Integer maxGrprice;
  private Integer minGrout; //관리비 지출
  private Integer maxGrout;

  //  면적 필터
  private Double minAreaPyeng; //토지면적
  private Double maxAreaPyeng;
  private Integer minYeonAreaPyeng;  // 연면적
  private Integer maxYeonAreaPyeng;
  private Integer minBuildingAreaPyeng; // 건축면적
  private Integer maxBuildingAreaPyeng;
  private Integer minYongjeokAreaPyeong;   // 용적률산정 연면적
  private Integer maxYongjeokAreaPyeong;

  // 지번필터
  private String minJibun;
  private String maxJibun;

}
