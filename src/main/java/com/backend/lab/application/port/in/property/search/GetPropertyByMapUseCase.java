package com.backend.lab.application.port.in.property.search;

import com.backend.lab.api.admin.property.core.dto.req.PropertyMapListReq;
import com.backend.lab.api.member.common.property.wishlist.dto.PropertyListResp;
import com.backend.lab.common.entity.dto.resp.ListResp;
import java.util.List;

public interface GetPropertyByMapUseCase {
    ListResp<PropertyListResp> getByMap(PropertyMapListReq req, List<Long> adminIds, List<Long> bigCategoryIds, List<Long> smallCategoryIds);
}
