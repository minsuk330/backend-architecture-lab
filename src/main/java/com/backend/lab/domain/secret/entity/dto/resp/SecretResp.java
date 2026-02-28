package com.backend.lab.domain.secret.entity.dto.resp;

import com.backend.lab.common.entity.dto.resp.BaseResp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SecretResp extends BaseResp {

  private String content;
  private String createdName;
  private Long createdId;
}
