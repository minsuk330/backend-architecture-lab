package com.backend.lab.common.s3.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreSignedUrlResp {

  private String fileName;
  private String uploadUrl;
  private String accessUrl;
  private String contentDisposition;
}
