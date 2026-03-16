package com.backend.lab.application.port.out.openapi;

import com.backend.lab.common.openapi.dto.landCharacteristic.LandCharacteristicResp;
import java.util.concurrent.CompletableFuture;

public interface LandCharacteristicApiPort {
    CompletableFuture<LandCharacteristicResp> getLandCharacteristicInfo(String pnu);
}
