package com.backend.lab.api.admin.session.dto.req;

import com.backend.lab.domain.admin.core.entity.vo.AdminRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GetAdminSessionOptions {

  private AdminRole adminRole;
}
