package com.backend.lab.application.port.in.property.advertisement;

public interface RemoveAdvertisementUseCase {
    void remove(Long propertyId, Long agentId);
}
