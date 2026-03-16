package com.backend.lab.common.openapi.service.toji;

import com.backend.lab.common.openapi.dto.landUsePlan.LandUseResp;
import java.util.concurrent.CompletableFuture;

public interface LandUsePlanApiPort {
    CompletableFuture<LandUseResp> getLandUsePlanInfo(String pnu);
}
