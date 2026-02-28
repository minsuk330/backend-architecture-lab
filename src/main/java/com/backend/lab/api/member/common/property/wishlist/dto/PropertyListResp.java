package com.backend.lab.api.member.common.property.wishlist.dto;

import com.backend.lab.api.admin.property.core.dto.resp.PropertyAddressResp;
import com.backend.lab.common.entity.dto.resp.BaseResp;
import com.backend.lab.domain.property.core.entity.vo.LedgerType;
import com.backend.lab.domain.property.core.entity.vo.PropertyStatus;
import com.backend.lab.domain.uploadFile.entity.dto.resp.UploadFileResp;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PropertyListResp extends BaseResp {

  //찜 여부
  private Boolean isMywish;
  private Long wishCount;
  private String wishGroupName;
  private String buildingName;
  private LedgerType ledgerType;
  private Long mmPrice;

  private Long pyeongPrice;//평단가

  private Integer minFloor;
  private Integer maxFloor;
  private PropertyStatus status;

  private Double areaPyeng;
  private Double areaMeter;

  private Double yeonAreaPyeng;
  private Double yeonAreaMeter;

  private Double buildingAreaPyeng;
  private Double buildingAreaMeter;

  private PropertyAddressResp propertyAddressResp;

  private UploadFileResp thumbnailImage;

  //담당자 추가
  private String adminName;

  private LocalDateTime confirmedAt;

  private LocalDateTime startDate;
  private LocalDateTime endDate;

}
