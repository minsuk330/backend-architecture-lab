package com.backend.lab.domain.agentItem.history.contactItem.service;

import com.backend.lab.domain.agentItem.history.contactItem.entity.ContactItemHistory;
import com.backend.lab.domain.agentItem.history.contactItem.entity.dto.ContactHistorySearchOptions;
import com.backend.lab.domain.agentItem.history.contactItem.entity.dto.ContactItemHistoryResp;
import com.backend.lab.domain.agentItem.history.contactItem.entity.dto.ContactItemHistoryResp.ContactHistoryCustomer;
import com.backend.lab.domain.agentItem.history.contactItem.repository.ContactItemHistoryRepository;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.entity.embedded.AgentProperties;
import com.backend.lab.domain.member.core.entity.embedded.CustomerProperties;
import com.backend.lab.domain.member.core.entity.vo.CompanyType;
import com.backend.lab.domain.member.core.service.CustomerService;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.property.core.entity.embedded.AddressProperties;
import com.backend.lab.domain.property.core.entity.information.AddressInformation;
import com.backend.lab.domain.propertyMember.entity.PropertyMember;
import com.backend.lab.domain.propertyMember.service.PropertyMemberService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContactItemHistoryService {

  private final ContactItemHistoryRepository contactItemHistoryRepository;
  private final PropertyMemberService propertyMemberService;
  private final CustomerService customerService;

  public Page<ContactItemHistory> search(ContactHistorySearchOptions options) {
    return contactItemHistoryRepository.search(options);
  }

  public Optional<ContactItemHistory> getByPropertyAndAgent(Long propertyId, Long userId) {
    return contactItemHistoryRepository.findByPropertyIdAndAgentId(propertyId, userId);
  }

  public ContactItemHistoryResp itemHistoryResp(ContactItemHistory itemHistory) {

    Property property = itemHistory.getProperty();
    Member agent = itemHistory.getAgent();

    String agentName = null;
    if (agent != null) {
      AgentProperties agentProperties = agent.getAgentProperties();
      if (agentProperties != null) {
        agentName = agentProperties.getBusinessName();
      }
    }

    Long propertyId = null;
    String buildingName = null;

    String address = null;

    List<ContactHistoryCustomer> customers = new ArrayList<>();
    if (property != null) {
      propertyId = property.getId();
      buildingName = property.getBuildingName();
      AddressInformation addressInfo = property.getAddress();
      if (addressInfo != null) {
        AddressProperties properties = addressInfo.getProperties();
        if (properties != null) {
          address = properties.getRoadAddress();
          if (address == null) {
            address = properties.getJibunAddress();
          }
        }
      }

      List<PropertyMember> propertyMembers = propertyMemberService.getByProperty(propertyId);
      for (PropertyMember pm : propertyMembers) {
        Long memberId = pm.getMemberId();
        Member customer = customerService.getById(memberId);
        CustomerProperties customerProperties = customer.getCustomerProperties();

        String name = null;
        String phoneNumber = null;
        CompanyType companyType = null;
        if (customerProperties != null) {
          name = customerProperties.getName();
          phoneNumber = customerProperties.getPhoneNumber();
          companyType = customerProperties.getCompanyType();
        }

        customers.add(
            ContactHistoryCustomer.builder()
                .name(name)
                .phoneNumber(phoneNumber)
                .companyType(companyType)
                .build()
        );
      }
    }

    return ContactItemHistoryResp.builder()
        .id(itemHistory.getId())
        .createdAt(itemHistory.getCreatedAt())
        .agentName(agentName)
        .propertyId(propertyId)
        .buildingName(buildingName)
        .address(address)
        .isUsed(itemHistory.getIsUsed())
        .beforeCount(itemHistory.getBeforeCount())
        .afterCount(itemHistory.getAfterCount())

        .customers(customers)
        .build();
  }

  @Transactional
  public void save(ContactItemHistory contactItemHistory) {
    contactItemHistoryRepository.save(contactItemHistory);
  }
}
