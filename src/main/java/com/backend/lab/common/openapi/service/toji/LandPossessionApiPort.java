package com.backend.lab.common.openapi.service.toji;

import com.backend.lab.common.openapi.dto.landPossession.LandPossessionResp;
import java.util.concurrent.CompletableFuture;

public interface LandPossessionApiPort {
    CompletableFuture<LandPossessionResp> getLandOwnerInfo(String pnu);
}
