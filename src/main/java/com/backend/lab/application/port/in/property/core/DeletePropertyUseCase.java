package com.backend.lab.application.port.in.property.core;

public interface DeletePropertyUseCase {
    void delete(Long propertyId, Long userId, String clientIp);
}
