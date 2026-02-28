package com.backend.lab.api.member.common.property.wishlist.controller;

import static com.backend.lab.common.util.AuthUtil.getUserId;

import com.backend.lab.api.member.common.property.wishlist.dto.PropertyListResp;
import com.backend.lab.api.member.common.property.wishlist.dto.WishlistToggleResp;
import com.backend.lab.api.member.common.property.wishlist.facade.MemberWishlistFacade;
import com.backend.lab.common.entity.dto.req.PageOptions;
import com.backend.lab.common.entity.dto.resp.PageResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member/wishlist")
@Tag(name = "[회원/공용] 관심 매물")
public class MemberWishlistController {

  private final MemberWishlistFacade memberWishlistFacade;

  @Operation(summary = "관심매물 목록조회")
  @GetMapping
  public PageResp<PropertyListResp> list(
      @ParameterObject @ModelAttribute PageOptions options
  ) {
    return memberWishlistFacade.memberWishlist(options, getUserId());
  }

  @Operation(summary = "관심매물 토글 (추가/삭제)")
  @PatchMapping("/{propertyId}")
  public WishlistToggleResp toggle(@PathVariable("propertyId") Long propertyId) {
    return memberWishlistFacade.toggleWishlist(propertyId, getUserId());
  }

}
