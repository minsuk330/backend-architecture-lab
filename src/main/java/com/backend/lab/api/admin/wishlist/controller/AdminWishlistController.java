package com.backend.lab.api.admin.wishlist.controller;

import com.backend.lab.api.admin.wishlist.dto.req.SearchWishlistOptions;
import com.backend.lab.api.admin.wishlist.dto.resp.WishlistGroupResp;
import com.backend.lab.api.admin.wishlist.facade.AdminWishlistFacade;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.property.core.entity.dto.resp.PropertySearchResp;
import com.backend.lab.domain.wishlistGroup.entity.dto.req.WishlistGroupCreateReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/wishlist")
@Tag(name = "[관리자] 관심그룹 관리")
public class
AdminWishlistController {

  private final AdminWishlistFacade adminWishlistFacade;

  @Operation(summary = "관심그룹 조회")
  @GetMapping
  public List<WishlistGroupResp> getAllWishlistGroups() {
    return adminWishlistFacade.getAllWishlistGroups();
  }

  @PreAuthorize("hasAuthority('MANAGE_INTEREST_GROUP')")
  @Operation(summary = "관심그룹 여러개 저장/수정")
  @PutMapping
  public void createWishlistGroups(
      @RequestBody List<WishlistGroupCreateReq> reqs
  ) {
    adminWishlistFacade.createWishlistGroups(reqs);
  }

  @PreAuthorize("hasAuthority('MANAGE_INTEREST_GROUP')")
  @Operation(summary = "관심 그룹 삭제")
  @DeleteMapping("/{groupId}")
  public void deleteWishlistGroup(
      @PathVariable("groupId") Long groupId
  ) {
    adminWishlistFacade.deleteWishlist(groupId);
  }

  @Operation(summary = "관심 매물 조회")
  @GetMapping("/list")
  public PageResp<PropertySearchResp> search(
      @ModelAttribute SearchWishlistOptions options
  ) {
    return adminWishlistFacade.search(options);
  }

}
