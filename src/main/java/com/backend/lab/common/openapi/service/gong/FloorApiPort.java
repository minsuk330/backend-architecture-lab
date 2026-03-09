package com.backend.lab.common.openapi.service.gong;

import com.backend.lab.api.admin.property.info.dto.resp.PropertyFloorInfoItem;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface FloorApiPort {
    CompletableFuture<List<PropertyFloorInfoItem>> getFloorInfo(String pnu);
}
