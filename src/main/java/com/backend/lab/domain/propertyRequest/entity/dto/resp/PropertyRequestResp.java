package com.backend.lab.domain.propertyRequest.entity.dto.resp;

import com.backend.lab.common.entity.dto.resp.BaseResp;
import com.backend.lab.domain.propertyRequest.entity.vo.RequestStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@SuperBuilder
@AllArgsConstructor
public class PropertyRequestResp extends BaseResp {

  private String buildingName;
  private String jibunAddress;
  private String roadAddress;

  private Integer mmPrice;
  private Integer depositPrice;
  private Integer monthPrice;

  private RequestStatus status;

  private LocalDateTime approvedAt;
  private LocalDateTime rejectedAt;

}
