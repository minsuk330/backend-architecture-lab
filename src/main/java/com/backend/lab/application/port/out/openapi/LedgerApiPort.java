package com.backend.lab.application.port.out.openapi;

import com.backend.lab.api.admin.property.info.dto.BuildingInfo;
import com.backend.lab.domain.property.core.entity.embedded.LedgerProperties;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface LedgerApiPort {
    CompletableFuture<List<BuildingInfo>> getBuildingInfo(String pnu);
    CompletableFuture<List<LedgerProperties>> getLedgerInfo(String pnu, List<BuildingInfo> buildings);
}
