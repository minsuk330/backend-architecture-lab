package com.backend.lab.domain.agentItem.history.advItem.service;

import com.backend.lab.domain.agentItem.history.advItem.entity.AdvertiseItemHistory;
import com.backend.lab.domain.agentItem.history.advItem.entity.dto.AdvertiseHistorySearchOptions;
import com.backend.lab.domain.agentItem.history.advItem.entity.dto.AdvertiseItemHistoryResp;
import com.backend.lab.domain.agentItem.history.advItem.repository.AdvertiseItemHistoryRepository;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.entity.embedded.AgentProperties;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.propertyAdvertisement.entity.PropertyAdvertisement;
import com.backend.lab.domain.purchase.core.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdvertiseItemHistoryService {

  private final AdvertiseItemHistoryRepository advertiseItemHistoryRepository;
  private final PurchaseService purchaseService;

  public Page<AdvertiseItemHistory> search(AdvertiseHistorySearchOptions options) {
    return advertiseItemHistoryRepository.search(options);
  }

  public AdvertiseItemHistory create(PropertyAdvertisement adv, Integer beforeCount, Integer afterCount) {
    return advertiseItemHistoryRepository.save(
        AdvertiseItemHistory.builder()
            .propertyAdvertisement(adv)
            .beforeCount(beforeCount)
            .afterCount(afterCount)
            .build()
    );
  }

  public AdvertiseItemHistoryResp advertiseHistoryResp(AdvertiseItemHistory advertiseItemHistory) {

    PropertyAdvertisement propertyAdvertisement = advertiseItemHistory.getPropertyAdvertisement();

    String agentName = null;
    String businessName = null;
    Long uid = null;
    Member agent = propertyAdvertisement.getAgent();
    if (agent != null) {
      AgentProperties agentProperties = agent.getAgentProperties();
      uid = agent.getId();
      if (agentProperties != null) {
        agentName = agentProperties.getName();
        businessName = agentProperties.getBusinessName();
      }
    }
    Long propertyId = null;
    String buildingName = null;
    Property property = propertyAdvertisement.getProperty();
    if (property != null) {
      propertyId = property.getId();
      buildingName = property.getBuildingName();
    }


    //어떤 광고인지 알아야 한다.
    //purchaseproduct에서 이름 가져와야 하는데..

    return AdvertiseItemHistoryResp.builder()
        .id(propertyAdvertisement.getId())

//        .productName() 이거 못찾겠음,,다른건 다 수정해놓음
        .uid(uid)
        .businessName(businessName)
        .startAt(propertyAdvertisement.getStartDate().toLocalDate())
        .endAt(propertyAdvertisement.getEndDate().toLocalDate())
        .agentName(agentName)
        .propertyId(propertyId)
        .buildingName(buildingName)
        .beforeCount(advertiseItemHistory.getBeforeCount())
        .afterCount(advertiseItemHistory.getAfterCount())
        .build();
  }
}
