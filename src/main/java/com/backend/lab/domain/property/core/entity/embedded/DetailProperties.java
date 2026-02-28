package com.backend.lab.domain.property.core.entity.embedded;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@Schema(description = "상세 정보")
public class DetailProperties {

  @Schema(description = "광고 노출용 제목")
  private String adTitle;

  @Schema(description = "내용")
  @Column(length = 65535)
  private String content;

  @Schema(description = "기타 특징")
  @Column(length = 2000)
  private String etc;


  @Schema(description = "비밀 메모")
  @Column(length = 2000)
  private String secretMemo;
}
