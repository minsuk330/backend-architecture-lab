package com.backend.lab.common.openapi.service.tojiList;

import com.backend.lab.api.admin.property.info.dto.resp.PropertyMoveInfoItem;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface LandMoveApiPort {
    CompletableFuture<List<PropertyMoveInfoItem>> getLandMovesInfo(String pnu);
}
