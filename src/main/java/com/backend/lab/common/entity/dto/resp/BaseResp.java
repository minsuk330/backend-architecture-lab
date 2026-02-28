package com.backend.lab.common.entity.dto.resp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BaseResp {

  private Long id;
  @JsonInclude(Include.NON_NULL)
  private LocalDateTime createdAt;
  @JsonInclude(Include.NON_NULL)
  private LocalDateTime updatedAt;
  @JsonInclude(Include.NON_NULL)
  private LocalDateTime deletedAt;
}
