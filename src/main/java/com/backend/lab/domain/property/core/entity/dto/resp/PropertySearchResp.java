package com.backend.lab.domain.property.core.entity.dto.resp;

import com.backend.lab.api.admin.property.core.dto.resp.PropertyAddressResp;
import com.backend.lab.common.entity.dto.resp.BaseResp;
import com.backend.lab.domain.property.core.entity.vo.LedgerType;
import com.backend.lab.domain.property.core.entity.vo.PropertyStatus;
import com.backend.lab.domain.property.sale.entity.dto.SaleResp;
import com.backend.lab.domain.uploadFile.entity.dto.resp.UploadFileResp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PropertySearchResp extends BaseResp {

  //대표 이미지
  private UploadFileResp thumbnailImage;

  private SaleResp sale;
  //링크 용도
  private Boolean isPublic;
  private PropertyStatus propertyStatus;
  private LocalDate completedAt; // 완료일
  private LocalDate pendingAt; // 보류일

  private String wishGroupName;
  private Long wishCount;

  //건물정보
  private String majorName;
  private String minorName;
  private String buildingName;
  private Integer minFloor;
  private Integer maxFloor;
  private LedgerType ledgerType; //등기 미등기 등등

  //면적(//이거 테스트 해야 함
  private Double areaPyeng;//대지면적 리스트 조회해서 전부 더해줘야함
  private Double buildingAreaPyeng;//건축면적 //이거도 마찬가지인가?
  private Double yeonAreaPyeng; //
  private Double landAreaPyeong;

  //금액
  private Long mmPrice;
  private Long pyengPrice;
  private Double roi;

  //용도
  //건물에서 주용도
  private String mainPurpsCdNm;
  //토지에서 용도지역
  private String yongdo;

  private String adminName;
  //주소
  private PropertyAddressResp propertyAddressResp;
  private LocalDateTime confirmedAt;


}
