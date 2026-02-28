package com.backend.lab.domain.uploadFile.entity.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadFileReq {

  private Long id;
  private String fileName;
  private String fileUrl;
}
