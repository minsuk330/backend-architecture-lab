package com.backend.lab.domain.member.core.entity.dto.resp;

import com.backend.lab.common.entity.dto.resp.BaseResp;
import com.backend.lab.domain.member.core.entity.vo.MemberType;
import com.backend.lab.domain.member.core.entity.vo.ProviderType;
import com.backend.lab.domain.uploadFile.entity.dto.resp.UploadFileResp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResp extends BaseResp {

  private String email;
  private UploadFileResp profileImage;
  private MemberType role;
  private ProviderType provider;

  private boolean isBlocked;
  private boolean isFirstLogin;

  private MemberType type;
}
