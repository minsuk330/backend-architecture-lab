package com.backend.lab.api.admin.property.core.dto.resp;

import com.backend.lab.domain.uploadFile.entity.dto.resp.UploadFileResp;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PropertyTemplateResp {
  @Schema(description = "정보1 파일")
  private UploadFileResp info1Url;

  @Schema(description = "정보2 파일")
  private UploadFileResp info2Url;

  @Schema(description = "평면도(부분) 파일")
  private UploadFileResp planPartUrl;

  @Schema(description = "평면도(전체) 파일")
  private UploadFileResp planEntireUrl;

  @Schema(description = "기타1 파일")
  private UploadFileResp etc1Url;

  @Schema(description = "기타2 파일")
  private UploadFileResp etc2Url;

  @Schema(description = "기타3 파일")
  private UploadFileResp etc3Url;

  @Schema(description = "기타4 파일")
  private UploadFileResp etc4Url;

  @Schema(description = "기타5 파일")
  private UploadFileResp etc5Url;

  @Schema(description = "기타6 파일")
  private UploadFileResp etc6Url;

  @Schema(description = "기타7 파일")
  private UploadFileResp etc7Url;

  @Schema(description = "기타8 파일")
  private UploadFileResp etc8Url;

  @Schema(description = "기타9 파일")
  private UploadFileResp etc9Url;

  @Schema(description = "기타10 파일")
  private UploadFileResp etc10Url;

}
