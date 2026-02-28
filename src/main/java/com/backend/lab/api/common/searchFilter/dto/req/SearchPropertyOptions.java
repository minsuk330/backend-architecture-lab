package com.backend.lab.api.common.searchFilter.dto.req;

import com.backend.lab.api.admin.property.core.dto.req.AreaFilter;
import com.backend.lab.api.admin.property.core.dto.req.PriceFilter;
import com.backend.lab.api.common.searchFilter.dto.vo.SortDirection;
import com.backend.lab.api.common.searchFilter.dto.vo.SortType;
import com.backend.lab.common.entity.dto.req.PageOptions;
import com.backend.lab.domain.member.core.entity.vo.MemberType;
import com.backend.lab.domain.property.core.entity.vo.PropertyStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springdoc.core.annotations.ParameterObject;


@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ParameterObject
public class SearchPropertyOptions extends PageOptions implements AreaFilter, PriceFilter {

  private String query; //건물명, 업종, 메모 검색

  private String sidoCode;
  private String sigungnCode;
  private String bjdCode;

  //맨 윗칸 검색 필터
  //오름, 내림
  private SortDirection sortDirection;
  //생성일, 수정일 등등
  private SortType sortType;

  private MemberType memberType;

  private Boolean isPublic;

  // 진행상태 필터
  private PropertyStatus status;

  // 수익률 범위
  private Double minRoi;
  private Double maxRoi;

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


  //전속필터
  private Long exclusiveId;

  // 지번필터
  private String minJibun;
  private String maxJibun;



}
