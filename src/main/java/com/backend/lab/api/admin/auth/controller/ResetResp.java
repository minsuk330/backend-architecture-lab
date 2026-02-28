package com.backend.lab.api.admin.auth.controller;

import com.backend.lab.domain.member.core.entity.vo.ProviderType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ResetResp {

  private boolean isSocial;
  private ProviderType providerType;

}
