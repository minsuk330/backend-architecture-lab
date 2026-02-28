package com.backend.lab.domain.wishlist.service;

import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.wishlist.entity.Wishlist;
import com.backend.lab.domain.wishlist.repository.WishlistRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishlistService {

  private final WishlistRepository wishlistRepository;

  public Wishlist getById(Long propertyWishlistId) {
    return wishlistRepository.findById(propertyWishlistId)
        .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "propertyWishlist"));
  }

  @Transactional
  public void create(Property property, Member member) {
    wishlistRepository.save(
        Wishlist.builder()
            .property(property)
            .member(member)
            .build(
            ));
  }

  public Wishlist isMyWishlist(Long memberId) {
    return wishlistRepository.getByMemberId(memberId).orElse(null);
  }

  //매물의 관심 수
  public Wishlist getByMemberAndProperty(Long memberId, Long propertyId) {
    return wishlistRepository.findByMemberAndProperty(memberId, propertyId).orElse(null);
  }

  public Set<Long> getWishlistPropertyIds(Long memberId, List<Long> propertyIds) {
    if (propertyIds == null || propertyIds.isEmpty()) {
      return Set.of();
    }
    
    List<Wishlist> wishlists = wishlistRepository.findByMemberIdAndPropertyIds(memberId, propertyIds);
    return wishlists.stream()
        .map(wishlist -> wishlist.getProperty().getId())
        .collect(Collectors.toSet());
  }

  @Transactional
  public void delete(Long memberId, Long propertyId) {
    Wishlist byMemberAndProperty = getByMemberAndProperty(memberId, propertyId);
    wishlistRepository.delete(byMemberAndProperty);
  }
  @Transactional
  public void delete(Wishlist wishlist) {
    wishlistRepository.delete(wishlist);
  }

}
