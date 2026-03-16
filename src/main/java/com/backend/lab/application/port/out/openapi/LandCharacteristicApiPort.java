package com.backend.lab.common.openapi.service.toji;

import com.backend.lab.common.openapi.dto.landCharacteristic.LandCharacteristicResp;
import java.util.concurrent.CompletableFuture;

public interface LandCharacteristicApiPort {
    CompletableFuture<LandCharacteristicResp> getLandCharacteristicInfo(String pnu);
}
