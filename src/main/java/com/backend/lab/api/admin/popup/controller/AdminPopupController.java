package com.backend.lab.api.admin.popup.controller;

import static com.backend.lab.common.util.AuthUtil.getUserId;

import com.backend.lab.api.admin.popup.facade.AdminPopupFacade;
import com.backend.lab.common.auth.annotation.RequireAdminRole;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.popup.entity.dto.req.PopupCreateReq;
import com.backend.lab.domain.popup.entity.dto.req.PopupUpdateReq;
import com.backend.lab.domain.popup.entity.dto.req.SearchPopupOptions;
import com.backend.lab.domain.popup.entity.dto.resp.PopupResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequireAdminRole
@RestController
@RequestMapping("/admin/popup")
@RequiredArgsConstructor
@Tag(name = "[관리자] 팝업 관리")
public class AdminPopupController {

  private final AdminPopupFacade adminPopupFacade;

  @Operation(summary = "팝업 검색")
  @GetMapping
  public PageResp<PopupResp> search(
      @ModelAttribute SearchPopupOptions options
  ) {
    return adminPopupFacade.search(options);
  }

  @Operation(summary = "팝업 상세 조회")
  @GetMapping("/{popupId}")
  public PopupResp getById(
      @PathVariable("popupId") Long popupId
  ) {
    return adminPopupFacade.getById(popupId);
  }

  @PreAuthorize("hasAuthority('MANAGE_POPUP')")
  @Operation(summary = "팝업 생성")
  @PostMapping
  public void create(
      @RequestBody PopupCreateReq req
  ) {
    adminPopupFacade.create(getUserId(), req);
  }

  @PreAuthorize("hasAuthority('MANAGE_POPUP')")
  @Operation(summary = "팝업 수정")
  @PutMapping("/{popupId}")
  public void update(
      @PathVariable("popupId") Long popupId,
      @RequestBody PopupUpdateReq req
  ) {
    adminPopupFacade.update(popupId, req);
  }

  @PreAuthorize("hasAuthority('MANAGE_POPUP')")
  @Operation(summary = "팝업 삭제")
  @DeleteMapping("/{popupId}")
  public void delete(
      @PathVariable("popupId") Long popupId
  ) {
    adminPopupFacade.delete(popupId);
  }
}
