package com.backend.lab.application.service.property.core;

import com.backend.lab.api.admin.property.info.dto.resp.PropertyFloorInfoItem;
import com.backend.lab.application.port.in.property.core.FloorUpdateUseCase;
import com.backend.lab.common.openapi.service.gong.FloorApiPort;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.property.core.repository.PropertyRepository;
import com.backend.lab.domain.property.core.service.PropertyService;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FloorUpdateService implements FloorUpdateUseCase {

    private final PropertyService propertyService;
    private final PropertyRepository propertyRepository;
    private final FloorApiPort floorApiPort;

    @Override
    @Async
    @Transactional
    public void floorUpdate() {
        List<Property> properties = propertyService.gets();

        Map<String, Property> pnuToPropertyMap = properties.stream()
            .filter(property -> property.getAddress() != null
                && property.getAddress().getProperties() != null
                && property.getAddress().getProperties().getPnu() != null)
            .collect(Collectors.toMap(
                property -> property.getAddress().getProperties().getPnu(),
                Function.identity(),
                (existing, replacement) -> existing
            ));

        int totalCount = pnuToPropertyMap.size();
        AtomicInteger processedCount = new AtomicInteger(0);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        log.info("Floor 업데이트 시작 - 총 {}개 처리 예정", totalCount);

        pnuToPropertyMap.forEach((pnu, property) -> {
            int currentProcessed = processedCount.incrementAndGet();

            try {
                CompletableFuture<List<PropertyFloorInfoItem>> floorResponse = floorApiPort.getFloorInfo(pnu);
                List<PropertyFloorInfoItem> floorInfo = floorResponse.get();

                if (floorInfo != null && !floorInfo.isEmpty()) {
                    property.getFloors().forEach(floorInformation -> {
                        if (floorInformation.getProperties() != null) {
                            String propertyFloor = floorInformation.getProperties().getFloor();
                            String upjong = floorInformation.getProperties().getUpjong();

                            floorInfo.stream()
                                .filter(apiFloor -> Objects.equals(apiFloor.getFloor(), propertyFloor)
                                    && Objects.equals(apiFloor.getUpjong(), upjong))
                                .findFirst()
                                .ifPresent(matchedFloor -> {
                                    Integer apiAreaPyung = matchedFloor.getAreaPyung() != null
                                        ? matchedFloor.getAreaPyung().intValue() : null;
                                    Integer apiAreaMeter = matchedFloor.getAreaMeter() != null
                                        ? matchedFloor.getAreaMeter().intValue() : null;
                                    Integer currentAreaPyung = floorInformation.getProperties().getAreaPyung() != null
                                        ? floorInformation.getProperties().getAreaPyung().intValue() : null;
                                    Integer currentAreaMeter = floorInformation.getProperties().getAreaMeter() != null
                                        ? floorInformation.getProperties().getAreaMeter().intValue() : null;

                                    boolean currentValuesEqual = Objects.equals(currentAreaPyung, currentAreaMeter);
                                    boolean apiValuesMatch = Objects.equals(apiAreaPyung, currentAreaPyung)
                                        && Objects.equals(apiAreaMeter, currentAreaMeter);
                                    boolean upJongMatch = Objects.equals(matchedFloor.getUpjong(),
                                        floorInformation.getProperties().getUpjong());

                                    if (currentValuesEqual || (apiValuesMatch && upJongMatch)) {
                                        floorInformation.getProperties().setAreaPyung(matchedFloor.getAreaPyung());
                                        floorInformation.getProperties().setAreaMeter(matchedFloor.getAreaMeter());
                                    }
                                });
                        }
                    });

                    propertyRepository.save(property);
                    successCount.incrementAndGet();
                }
            } catch (Exception e) {
                log.error("호출 실패 - PNU: {}, PropertyId: {}", pnu, property.getId());
            }

            if (currentProcessed % Math.max(1, totalCount / 10) == 0 || currentProcessed % 100 == 0) {
                double progressPercentage = (double) currentProcessed / totalCount * 100;
                log.info("Floor 업데이트 진행률: {}/{} ({}%) - 성공: {}, 실패: {}",
                    currentProcessed, totalCount, progressPercentage, successCount.get(), errorCount.get());
            }
        });
        log.info("#### 완 ####");
    }
}
