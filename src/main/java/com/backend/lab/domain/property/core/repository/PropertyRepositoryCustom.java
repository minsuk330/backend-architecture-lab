package com.backend.lab.domain.property.core.repository;

import com.backend.lab.api.admin.property.core.dto.req.PropertyMapListReq;
import com.backend.lab.api.admin.property.core.dto.req.PropertyStatReq;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyStatResp;
import com.backend.lab.api.admin.wishlist.dto.req.SearchWishlistOptions;
import com.backend.lab.api.common.searchFilter.dto.req.SearchPropertyOptions;
import com.backend.lab.api.member.common.property.core.dto.req.PropertyMemberOptions;
import com.backend.lab.common.entity.dto.req.PageOptions;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.property.core.entity.Property;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PropertyRepositoryCustom {

  Page<Property> search(SearchPropertyOptions options, List<Long> adminIds,
      List<Long> bigCategoryIds, List<Long> smallCategoryIds);

  Page<Property> findBynonMember(PropertyMemberOptions options);

  Page<Property> findBySeller(Long sellerId, PropertyMemberOptions options);

  Page<Property> findByBuyer(PropertyMemberOptions options);

  Page<Property> findByAgent(PropertyMemberOptions options);

  Page<Property> wishlistSearch(SearchWishlistOptions options);

  Page<Property> memberWishlist(PageOptions options, Long memberId);

  PropertyStatResp getStat(PropertyStatReq req);

  Page<Property> getsWithPropertyMember(Long memberId, Pageable pageable);

  List<Property> getsByMap(PropertyMapListReq req, List<Long> adminIds, List<Long> bigCategoryIds,
      List<Long> smallCategoryIds);

  List<Property> getsByMapWithSeller(PropertyMapListReq req, List<Long> adminIds, List<Long> bigCategoryIds, List<Long> smallCategoryIds,
      Member member);
}
