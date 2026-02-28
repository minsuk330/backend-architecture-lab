package com.backend.lab.domain.wishlistGroup.service;

import com.backend.lab.api.admin.wishlist.dto.resp.WishlistGroupResp;
import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import com.backend.lab.domain.wishlistGroup.entity.WishlistGroup;
import com.backend.lab.domain.wishlistGroup.entity.dto.req.WishlistGroupCreateReq;
import com.backend.lab.domain.wishlistGroup.repository.WishlistGroupRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishlistGroupService {

  private final WishlistGroupRepository wishlistGroupRepository;

  @Transactional
  public void create(WishlistGroupCreateReq req) {
    if (req.getMaxInterestCount() == null) {
      req.setMaxInterestCount(Integer.MAX_VALUE);
    } else if (req.getMaxInterestCount() < req.getMinInterestCount()) {
      throw new BusinessException(ErrorCode.BAD_REQUEST);
    }
    wishlistGroupRepository.save(
        WishlistGroup.builder()
            .groupName(req.getGroupName())
            .minInterestCount(req.getMinInterestCount())
            .maxInterestCount(req.getMaxInterestCount())
            .build()
    );
  }

  @Transactional
  public void createAll(List<WishlistGroupCreateReq> reqs) {
    // 1. 기존 시스템 그룹들 모두 삭제
    wishlistGroupRepository.deleteAll();
    
    // 2. 새로운 그룹들 생성
    for (WishlistGroupCreateReq req : reqs) {
      if (req.getMaxInterestCount() == null) {
        req.setMaxInterestCount(Integer.MAX_VALUE);
      } else if (req.getMaxInterestCount() < req.getMinInterestCount()) {
        throw new BusinessException(ErrorCode.BAD_REQUEST, "최대값은 최소값보다 커야 합니다.");
      }

      wishlistGroupRepository.save(
          WishlistGroup.builder()
              .groupName(req.getGroupName())
              .minInterestCount(req.getMinInterestCount())
              .maxInterestCount(req.getMaxInterestCount())
              .build()
      );
    }
  }

  public List<WishlistGroup> gets() {
    return wishlistGroupRepository.findAll();
  }

  @Transactional
  public void delete(Long groupId) {
    WishlistGroup wishlistGroup = this.getById(groupId);
    wishlistGroupRepository.delete(wishlistGroup);
  }

  @Transactional
  public void deleteAll() {
    wishlistGroupRepository.deleteAll();
  }

  public WishlistGroupResp wishlistGroupResp(WishlistGroup group) {
    return WishlistGroupResp.builder()
        .id(group.getId())
        .createdAt(group.getCreatedAt())
        .updatedAt(group.getUpdatedAt())

        .groupName(group.getGroupName())
        .maxInterestCount(group.getMaxInterestCount())
        .minInterestCount(group.getMinInterestCount())
        .build();
  }

  public String findGroupNameByWishCount(Long wishCount) {
    List<WishlistGroup> groups = gets();

    if (groups!=null && !groups.isEmpty()) {
      return groups.stream()
          .filter(group -> group.getMinInterestCount() <= wishCount
              && group.getMaxInterestCount() >= wishCount)
          .findFirst()
          .map(WishlistGroup::getGroupName)
          .orElse("그룹 없음");
    }
    return null;
  }

  public WishlistGroup findGroupByWishCount(Long wishCount) {
    List<WishlistGroup> groups = gets();
    
    return groups.stream()
        .filter(group -> group.getMinInterestCount() <= wishCount 
                      && group.getMaxInterestCount() >= wishCount)
        .findFirst()
        .orElse(null);
  }

  public WishlistGroup getById(Long groupId) {
    return wishlistGroupRepository.findById(groupId).orElse(null);
  }
}
