package com.backend.lab.api.admin.auth.dto.resp;

import com.backend.lab.api.admin.global.dto.resp.AdminGlobalResp;
import java.util.Collection;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

@Getter
@Setter
public class AdminAuthenticationToken extends UsernamePasswordAuthenticationToken {

  private AdminGlobalResp admin;

  public AdminAuthenticationToken(Object principal, Object credentials,
      Collection<? extends GrantedAuthority> authorities) {
    super(principal, credentials, authorities);
  }
}
