package com.backend.lab.application.port.in.property.search;

import com.backend.lab.api.member.common.property.wishlist.dto.PropertyListResp;
import com.backend.lab.common.entity.dto.resp.ListResp;

public interface GetPropertyListUseCase {
    ListResp<PropertyListResp> getList();
}
