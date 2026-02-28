package com.backend.lab.domain.property.core.entity.embedded;

import com.backend.lab.domain.uploadFile.entity.UploadFile;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
@Schema(description = "템플릿 파일")
public class TemplateProperties {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "info1_url_id")
  private UploadFile info1Url;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "info2_url_id")
  private UploadFile info2Url;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "plan_part_url_id")
  private UploadFile planPartUrl;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "plan_entire_url_id")
  private UploadFile planEntireUrl;

  @Schema(description = "기타1")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "etc1_url_id")
  private UploadFile etc1Url;

  @Schema(description = "기타2")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "etc2_url_id")
  private UploadFile etc2Url;

  @Schema(description = "기타3")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "etc3_url_id")
  private UploadFile etc3Url;

  @Schema(description = "기타4")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "etc4_url_id")
  private UploadFile etc4Url;

  @Schema(description = "기타5")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "etc5_url_id")
  private UploadFile etc5Url;

  @Schema(description = "기타6")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "etc6_url_id")
  private UploadFile etc6Url;

  @Schema(description = "기타7")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "etc7_url_id")
  private UploadFile etc7Url;

  @Schema(description = "기타8")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "etc8_url_id")
  private UploadFile etc8Url;

  @Schema(description = "기타9")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "etc9_url_id")
  private UploadFile etc9Url;

  @Schema(description = "기타10")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "etc10_url_id")
  private UploadFile etc10Url;
}
