package com.backend.lab.api.admin.wishlist.facade;

import com.backend.lab.api.admin.property.core.facade.AdminPropertyFacade;
import com.backend.lab.api.admin.property.core.mapper.PropertyMapper;
import com.backend.lab.api.admin.wishlist.dto.req.SearchWishlistOptions;
import com.backend.lab.api.admin.wishlist.dto.resp.WishlistGroupResp;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.property.core.entity.dto.resp.PropertySearchResp;
import com.backend.lab.domain.property.core.service.PropertyService;
import com.backend.lab.domain.wishlist.service.WishlistService;
import com.backend.lab.domain.wishlistGroup.entity.WishlistGroup;
import com.backend.lab.domain.wishlistGroup.entity.dto.req.WishlistGroupCreateReq;
import com.backend.lab.domain.wishlistGroup.service.WishlistGroupService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminWishlistFacade {

  private final WishlistGroupService wishlistGroupService;
  private final WishlistService wishlistService;
  private final PropertyService propertyService;
  private final AdminPropertyFacade adminPropertyFacade;
  private final PropertyMapper propertyMapper;

  //관심그룹 여러개 한번에 저장/수정
  @Transactional
  public void createWishlistGroups(List<WishlistGroupCreateReq> reqs) {
    wishlistGroupService.createAll(reqs);
  }

  //관심그룹 조회
  public List<WishlistGroupResp> getAllWishlistGroups() {
    List<WishlistGroup> groups = wishlistGroupService.gets();
    return groups.stream().map(
        wishlistGroupService::wishlistGroupResp).toList();
  }
  //관심그룹 삭제
  @Transactional
  public void deleteWishlist(Long groupId) {
    wishlistGroupService.delete(groupId);
  }

  //조회
  public PageResp<PropertySearchResp> search(SearchWishlistOptions options) {
    Page<Property> page =  propertyService.wishlistSearch(options);

    List<PropertySearchResp> list = page.stream().map(propertyMapper::propertySearchResp
    ).toList();

    return new PageResp<>(page, list);
  }
}
