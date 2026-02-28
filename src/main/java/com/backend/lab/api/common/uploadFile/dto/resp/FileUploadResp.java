package com.backend.lab.api.common.uploadFile.dto.resp;

import com.backend.lab.domain.uploadFile.entity.dto.resp.UploadFileResp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResp {

  private UploadFileResp file;
  private String uploadUrl;
  private String contentDisposition;
}
