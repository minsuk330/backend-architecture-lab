package com.backend.lab.api.admin.PropertyRequest.dto.resp;

import com.backend.lab.domain.propertyRequest.entity.dto.resp.PropertyRequestResp;
import com.backend.lab.domain.uploadFile.entity.dto.resp.UploadFileResp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@SuperBuilder
@AllArgsConstructor
public class PropertyRequestAgentResp extends PropertyRequestResp {

  private Long agentId;

  private String name;
  private String phoneNumber;

  private String agentName;
  private String businessName;

  private UploadFileResp contract;
}
