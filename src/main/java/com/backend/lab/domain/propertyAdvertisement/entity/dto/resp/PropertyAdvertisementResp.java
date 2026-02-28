package com.backend.lab.domain.propertyAdvertisement.entity.dto.resp;

import com.backend.lab.domain.uploadFile.entity.dto.resp.UploadFileResp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PropertyAdvertisementResp {

  private Long id;
  private Long agentId;
  private String name;
  private String businessName;
  private String phoneNumber;
  private String address;
  private UploadFileResp profileImage;
}
