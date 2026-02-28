package com.backend.lab.api.member.agent.propertyModal.facade;

import com.backend.lab.api.admin.property.customer.dto.CustomerPropertyResp;
import com.backend.lab.api.member.agent.propertyModal.facade.dto.resp.AgentPropertyContactResp;
import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import com.backend.lab.domain.agentItem.core.entity.AgentItem;
import com.backend.lab.domain.agentItem.core.entity.vo.AgentTicketStatus;
import com.backend.lab.domain.agentItem.core.service.AgentItemService;
import com.backend.lab.domain.agentItem.history.advItem.service.AdvertiseItemHistoryService;
import com.backend.lab.domain.agentItem.history.contactItem.entity.ContactItemHistory;
import com.backend.lab.domain.agentItem.history.contactItem.service.ContactItemHistoryService;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.entity.embedded.CustomerProperties;
import com.backend.lab.domain.member.core.entity.vo.CompanyType;
import com.backend.lab.domain.member.core.entity.vo.MemberType;
import com.backend.lab.domain.member.core.service.AgentService;
import com.backend.lab.domain.member.core.service.CustomerService;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.property.core.service.PropertyService;
import com.backend.lab.domain.property.taskNote.entity.TaskNote;
import com.backend.lab.domain.property.taskNote.entity.dto.resp.TaskNoteResp;
import com.backend.lab.domain.property.taskNote.service.TaskNoteService;
import com.backend.lab.domain.propertyAdvertisement.entity.PropertyAdvertisement;
import com.backend.lab.domain.propertyAdvertisement.service.PropertyAdvertisementService;
import com.backend.lab.domain.propertyMember.entity.PropertyMember;
import com.backend.lab.domain.propertyMember.service.PropertyMemberService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AgentPropertyModalFacade {

  private final AgentService agentService;
  private final PropertyMemberService propertyMemberService;

  private final AgentItemService agentItemService;
  private final ContactItemHistoryService contactHistoryService;
  private final AdvertiseItemHistoryService advHistoryService;
  private final PropertyAdvertisementService advService;
  private final CustomerService customerService;
  private final PropertyService propertyService;
  private final TaskNoteService taskNoteService;

  public AgentPropertyContactResp getCustomers(Long propertyId, Long userId) {
    Member agent = agentService.getById(userId);
    AgentItem agentItem = agentItemService.getByAgent(agent);
    Property defaultProperty = propertyService.getById(propertyId);

    AgentTicketStatus status = agentItemService.getTicketStatus(agentItem);
    List<PropertyMember> propertyMembers = propertyMemberService.getByProperty(propertyId);

    List<TaskNote> taskNotes = taskNoteService.getByProperty(propertyId);

    List<TaskNoteResp> taskResp = taskNotes.stream()
        .map(taskNote -> taskNoteService.taskNoteResp(taskNote))
        .limit(5)
        .toList();

    if (Objects.equals(defaultProperty.getExclusiveAgentId(), userId)) {

      if (!propertyMembers.isEmpty()) {
        List<Long> memberIds = propertyMembers.stream().map(PropertyMember::getMemberId
        ).toList();
        List<Member> memberList = memberIds.stream().map(customerService::getById).toList();

        List<CustomerPropertyResp> list = memberList.stream()
            .map(this::customerPropertyResp).toList();
        return AgentPropertyContactResp.builder()
            .canView(true)
            .taskNotes(taskResp)
            .customers(list)
            .build();
      }

    }


    // 이용권 사용중 아님
    if (!status.equals(AgentTicketStatus.USING)) {
      return AgentPropertyContactResp.builder()
          .canView(false)
          .customers(List.of())
          .build();
    }

    Optional<ContactItemHistory> optionalContactHistory = contactHistoryService.getByPropertyAndAgent(
        propertyId, userId);

    // 조회 기록 없음
    if (optionalContactHistory.isEmpty()) {
      return AgentPropertyContactResp.builder()
          .canView(false)
          .customers(List.of())
          .build();
    }

    ContactItemHistory contactItemHistory = optionalContactHistory.get();
    Property property = contactItemHistory.getProperty();

    List<CustomerPropertyResp> customers = new ArrayList<>();
    for (PropertyMember pm : propertyMembers) {
      Long memberId = pm.getMemberId();
      Member customer = customerService.getById(memberId);
      CustomerProperties customerProperties = customer.getCustomerProperties();

      String name = null;
      String phoneNumber = null;
      CompanyType companyType = null;
      String etc = null;
      if (customerProperties != null) {
        name = customerProperties.getName();
        phoneNumber = customerProperties.getPhoneNumber();
        companyType = customerProperties.getCompanyType();
        etc = customerProperties.getEtc();
      }

      customers.add(
          CustomerPropertyResp.builder()
              .name(name)
              .phoneNumber(phoneNumber)
              .companyType(companyType)
              .etc(etc)
              .build()
      );
    }

    return AgentPropertyContactResp.builder()
        .canView(true)
        .customers(customers)
        .taskNotes(taskResp)
        .build();
  }

  @Transactional
  public void view(Long propertyId, Long userId) {
    Member agent = agentService.getById(userId);
    AgentItem agentItem = agentItemService.getByAgent(agent);

    Optional<ContactItemHistory> optionalContactHistory = contactHistoryService.getByPropertyAndAgent(
        propertyId, userId);

    if (optionalContactHistory.isPresent()) {
      return;
    }

    Integer dailyFreeShowContactCount = agentItem.getDailyFreeShowContactCount();
    boolean hasFreeCount = dailyFreeShowContactCount > 0;

    if (!hasFreeCount && agentItem.getShowContactCount() <= 0) {
      throw new BusinessException(ErrorCode.LACK_OF_CONTACT_ITEM);
    }

    int beforeCount = agentItem.getShowContactCount();
    int afterCount = hasFreeCount ? beforeCount : beforeCount - 1;

    if (hasFreeCount) {
      int afterDailyFreeShowContactCount = dailyFreeShowContactCount - 1;
      agentItem.setDailyFreeShowContactCount(afterDailyFreeShowContactCount);
      agentItem.setLastUseDaily(LocalDateTime.now());
    } else {
      int afterShowContactCount = agentItem.getShowContactCount() - 1;
      agentItem.setShowContactCount(afterShowContactCount);
      agentItem.setLastUseDaily(LocalDateTime.now());
    }

    Property property = propertyService.getById(propertyId);

    ContactItemHistory contactItemHistory = ContactItemHistory.builder()
        .property(property)
        .agent(agent)
        .isUsed(!hasFreeCount)
        .beforeCount(beforeCount)
        .afterCount(afterCount)
        .build();
    contactHistoryService.save(contactItemHistory);
  }

  @Transactional
  public void addAdv(Long propertyId, Long userId) {
    Member agent = agentService.getById(userId);
    AgentItem agentItem = agentItemService.getByAgent(agent);

    Optional<PropertyAdvertisement> optionalAdv = advService.getByPropertyAndAgent(
        propertyId, userId);

    if (optionalAdv.isPresent()) {
      return;
    }

    Integer advertiseCount = agentItem.getAdvertiseCount();
    if (advertiseCount < 0) {
      throw new BusinessException(ErrorCode.LACK_OF_ADV_ITEM);
    }

    Property property = propertyService.getById(propertyId);
    PropertyAdvertisement adv = advService.create(property, agent);

    Integer afterCount = advertiseCount - 1;
    agentItem.setAdvertiseCount(afterCount);

    advHistoryService.create(adv, advertiseCount, afterCount);
  }


    private CustomerPropertyResp customerPropertyResp(Member member) {
      if (member.getType()== MemberType.CUSTOMER){
        return CustomerPropertyResp.builder()
            .name(member.getCustomerProperties().getName())
            .etc(member.getCustomerProperties().getEtc())
            .phoneNumber(member.getCustomerProperties().getPhoneNumber())
            .companyType(member.getCustomerProperties().getCompanyType())
            .build();
      }
      else {
        return CustomerPropertyResp.builder()
            .companyType(member.getSellerProperties().getCompanyType())
            .etc(member.getCustomerProperties().getEtc())
            .phoneNumber(member.getSellerProperties().getPhoneNumber())
            .name(member.getSellerProperties().getName())
            .build();
      }

    }
}
