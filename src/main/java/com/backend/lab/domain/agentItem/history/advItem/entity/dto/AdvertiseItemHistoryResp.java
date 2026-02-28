package com.backend.lab.domain.agentItem.history.advItem.entity.dto;

import com.backend.lab.common.entity.dto.resp.BaseResp;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AdvertiseItemHistoryResp extends BaseResp {

  private LocalDate startAt;
  private LocalDate endAt;
  private String agentName;
  private Long propertyId;
  private String buildingName;
  private Integer beforeCount;
  private Integer afterCount;

  private String productName;
  private Long uid;//agentId
  private String businessName;


}
