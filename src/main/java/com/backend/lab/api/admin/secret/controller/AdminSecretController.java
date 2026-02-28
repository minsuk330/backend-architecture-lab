package com.backend.lab.api.admin.secret.controller;

import static com.backend.lab.common.util.AuthUtil.getUserId;

import com.backend.lab.api.admin.secret.facade.AdminSecretFacade;
import com.backend.lab.common.auth.annotation.RequireAdminRole;
import com.backend.lab.common.entity.dto.resp.ListResp;
import com.backend.lab.domain.secret.entity.dto.req.SecretReq;
import com.backend.lab.domain.secret.entity.dto.resp.SecretResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequireAdminRole
@RestController
@RequestMapping("admin/secret")
@RequiredArgsConstructor
@Tag(name = "[관리자] 비밀 메모")
public class AdminSecretController {

  private final AdminSecretFacade adminSecretFacade;

  @Operation(summary = "비밀 메모 조회")
  @GetMapping("/{propertyId}")
  public ListResp<SecretResp> getSecret(
      @PathVariable("propertyId") Long propertyId
  ) {
    return adminSecretFacade.getSecret(propertyId, getUserId());
  }

  @Operation(summary = "비밀 메모 생성 or 수정")
  @PutMapping("/{propertyId}/register")
  public void createOrUpdateSecret (
      @PathVariable("propertyId") Long propertyId,
      @RequestBody SecretReq req
  ) {
    adminSecretFacade.createOrUpdate(req, propertyId, getUserId());
  }

  @Operation(summary = "비밀메모 삭제")
  @DeleteMapping("/{propertyId}")
  public void deleteSecret (
      @PathVariable("propertyId") Long propertyId
  ) {
    adminSecretFacade.deleteSecret(propertyId, getUserId());
  }


}
