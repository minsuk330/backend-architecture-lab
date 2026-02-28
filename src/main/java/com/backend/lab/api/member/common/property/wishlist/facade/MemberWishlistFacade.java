package com.backend.lab.api.member.common.property.wishlist.facade;

import com.backend.lab.api.admin.property.core.mapper.PropertyMapper;
import com.backend.lab.api.member.common.property.wishlist.dto.PropertyListResp;
import com.backend.lab.api.member.common.property.wishlist.dto.WishlistToggleResp;
import com.backend.lab.common.entity.dto.req.PageOptions;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.service.MemberService;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.property.core.service.PropertyService;
import com.backend.lab.domain.wishlist.entity.Wishlist;
import com.backend.lab.domain.wishlist.service.WishlistService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberWishlistFacade {

  private final WishlistService wishlistService;
  private final MemberService memberService;
  private final PropertyService propertyService;
  private final PropertyMapper propertyMapper;

  //회원이 관심 추가
  @Transactional
  public void createWishlist(Long propertyId, Long memberId) {
    Member member = memberService.getById(memberId);
    Property property = propertyService.getById(propertyId);

    long current = Optional.ofNullable(property.getWishCount()).orElse(0L);
    property.setWishCount(current + 1);
    wishlistService.create(property, member);
  }
  //회원 관심 삭제
  @Transactional
  public void deleteWishlist(Long propertyId, Long memberId) {

    Property property = propertyService.getById(propertyId);

    long current = Optional.ofNullable(property.getWishCount()).orElse(0L);
    property.setWishCount(current - 1);

    wishlistService.delete(memberId, propertyId);
  }

  public PageResp<PropertyListResp> memberWishlist(PageOptions options, Long memberId) {
    Page<Property> page = propertyService.memberWishlist(options, memberId);
    log.info("pages: {}", page.getTotalElements());
    Member member = memberService.getById(memberId);

    return propertyMapper.convertToPageResp(page, member);
  }

  @Transactional
  public WishlistToggleResp toggleWishlist(Long propertyId, Long memberId) {

    Wishlist wishlist = wishlistService.getByMemberAndProperty(memberId,
        propertyId);

    if (wishlist!=null) {
      // 이미 찜했으면 삭제
      deleteWishlist(propertyId, memberId);
      return WishlistToggleResp.builder()
          .isMyWish(false)
          .build();
    } else {
      // 찜하지 않았으면 추가
      createWishlist(propertyId, memberId);
      return WishlistToggleResp.builder()
          .isMyWish(true)
          .build();
    }

  }
}
