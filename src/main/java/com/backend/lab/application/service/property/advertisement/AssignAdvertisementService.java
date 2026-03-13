package com.backend.lab.application.service.property.advertisement;

import com.backend.lab.application.port.in.property.advertisement.AssignAdvertisementUseCase;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.service.MemberService;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.property.core.service.PropertyService;
import com.backend.lab.domain.propertyAdvertisement.service.PropertyAdvertisementService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AssignAdvertisementService implements AssignAdvertisementUseCase {

    private final PropertyService propertyService;
    private final MemberService memberService;
    private final PropertyAdvertisementService propertyAdvertisementService;

    @Override
    @Transactional
    public void assign(Long propertyId, Long agentId, LocalDateTime startDate, LocalDateTime endDate) {
        Property property = propertyService.getById(propertyId);
        Member agent = memberService.getById(agentId);
        propertyAdvertisementService.createAdvertisement(property, agent, startDate, endDate);
    }
}
