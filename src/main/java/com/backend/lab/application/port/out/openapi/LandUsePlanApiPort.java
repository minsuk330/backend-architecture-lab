package com.backend.lab.application.port.out.openapi;

import com.backend.lab.common.openapi.dto.landUsePlan.LandUseResp;
import java.util.concurrent.CompletableFuture;

public interface LandUsePlanApiPort {
    CompletableFuture<LandUseResp> getLandUsePlanInfo(String pnu);
}
