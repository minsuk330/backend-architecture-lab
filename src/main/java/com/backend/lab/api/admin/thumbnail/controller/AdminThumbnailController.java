package com.backend.lab.api.admin.thumbnail.controller;

import com.backend.lab.api.admin.thumbnail.facade.AdminThumbnailFacade;
import com.backend.lab.common.auth.annotation.RequireAdminRole;
import com.backend.lab.domain.thumbnail.entity.dto.req.ThumbnailReq;
import com.backend.lab.domain.thumbnail.entity.dto.resp.ThumbnailResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequireAdminRole
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/thumbnail")
@Tag(name = "[관리자] 썸네일 설정")
public class AdminThumbnailController {

  private final AdminThumbnailFacade adminThumbnailFacade;

  @Operation(summary = "썸네일 이미지 조회")
  @GetMapping
  public ThumbnailResp getThumbnail() {
    return adminThumbnailFacade.get();
  }

  @PreAuthorize("hasAuthority('MANAGE_MARKETING_THUMBNAIL')")
  @Operation(summary = "썸네일 이미지 설정")
  @PostMapping("/image")
  public void updateImage(
      @RequestBody ThumbnailReq req
  ) {
    adminThumbnailFacade.imageUpdate(req);
  }



}
