package com.backend.lab.application.port.in.property.advertisement;

import java.time.LocalDateTime;

public interface AssignAdvertisementUseCase {
    void assign(Long propertyId, Long agentId, LocalDateTime startDate, LocalDateTime endDate);
}
