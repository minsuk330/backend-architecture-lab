package com.backend.lab.api.admin.property.core.dto.req;

import com.backend.lab.domain.uploadFile.entity.UploadFile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
public class PropertyTemplateReq {
  @Schema(description = "정보1 파일 ID")
  private Long info1UrlId;

  @Setter
  @JsonIgnore
  private UploadFile info1Url;

  @Schema(description = "정보2 파일 ID")
  private Long info2UrlId;

  @Setter
  @JsonIgnore
  private UploadFile info2Url;

  @Schema(description = "평면도(부분) 파일 ID")
  private Long planPartUrlId;

  @Setter
  @JsonIgnore
  private UploadFile planPartUrl;

  @Schema(description = "평면도(전체) 파일 ID")
  private Long planEntireUrlId;

  @Setter
  @JsonIgnore
  private UploadFile planEntireUrl;

  @Schema(description = "기타1 파일 ID")
  private Long etc1UrlId;

  @Setter
  @JsonIgnore
  private UploadFile etc1Url;

  @Schema(description = "기타2 파일 ID")
  private Long etc2UrlId;

  @Setter
  @JsonIgnore
  private UploadFile etc2Url;

  @Schema(description = "기타3 파일 ID")
  private Long etc3UrlId;

  @Setter
  @JsonIgnore
  private UploadFile etc3Url;

  @Schema(description = "기타4 파일 ID")
  private Long etc4UrlId;

  @Setter
  @JsonIgnore
  private UploadFile etc4Url;

  @Schema(description = "기타5 파일 ID")
  private Long etc5UrlId;

  @Setter
  @JsonIgnore
  private UploadFile etc5Url;

  @Schema(description = "기타6 파일 ID")
  private Long etc6UrlId;

  @Setter
  @JsonIgnore
  private UploadFile etc6Url;

  @Schema(description = "기타7 파일 ID")
  private Long etc7UrlId;

  @Setter
  @JsonIgnore
  private UploadFile etc7Url;

  @Schema(description = "기타8 파일 ID")
  private Long etc8UrlId;

  @Setter
  @JsonIgnore
  private UploadFile etc8Url;

  @Schema(description = "기타9 파일 ID")
  private Long etc9UrlId;

  @Setter
  @JsonIgnore
  private UploadFile etc9Url;

  @Schema(description = "기타10 파일 ID")
  private Long etc10UrlId;

  @Setter
  @JsonIgnore
  private UploadFile etc10Url;

}
