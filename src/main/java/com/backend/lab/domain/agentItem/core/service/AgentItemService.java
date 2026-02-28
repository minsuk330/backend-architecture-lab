package com.backend.lab.domain.agentItem.core.service;

import com.backend.lab.api.member.agent.payment.facade.item.dto.AgentMyItemResp;
import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import com.backend.lab.domain.agentItem.core.entity.AgentItem;
import com.backend.lab.domain.agentItem.core.entity.AgentTicket;
import com.backend.lab.domain.agentItem.core.entity.vo.AgentTicketStatus;
import com.backend.lab.domain.agentItem.core.repository.AgentItemRepository;
import com.backend.lab.domain.agentItem.core.repository.AgentTicketRepository;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.purchase.item.entity.PurchaseProduct;
import com.backend.lab.domain.purchase.item.entity.vo.ProductType;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AgentItemService {

  private final AgentItemRepository agentItemRepository;
  private final AgentTicketRepository agentTicketRepository;

  public AgentItem getByAgent(Member agent) {
    return agentItemRepository.findByAgent(agent)
        .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "AgentItem"));
  }

  public Optional<AgentItem> getOptByAgent(Member agent) {
    return agentItemRepository.findByAgent(agent);
  }

  public AgentTicketStatus getTicketStatus(AgentTicket ticket) {
    LocalDateTime ticketStartAt = ticket.getTicketStartAt();
    LocalDateTime ticketEndAt = ticket.getTicketEndAt();

    LocalDateTime now = LocalDateTime.now();
    if (ticketEndAt.isBefore(now)) {
      return AgentTicketStatus.EXPIRED;
    }

    if(now.isAfter(ticketStartAt) && now.isBefore(ticketEndAt)) {
      return AgentTicketStatus.USING;
    }

    if(ticketStartAt.isAfter(now)) {
      return AgentTicketStatus.READY;
    }

    return null;
  }

  public AgentTicketStatus getTicketStatus(AgentItem item) {

    Set<AgentTicket> tickets = item.getTickets();
    AgentTicket currentTicket = this.getCurrentTicket(tickets);
    if (currentTicket != null) {
      return AgentTicketStatus.USING;
    }

    if (tickets.isEmpty()) {
      return AgentTicketStatus.REGISTER;
    }

    return AgentTicketStatus.REGISTER;
  }

  @Transactional
  public AgentItem createDefault(Member agent) {
    AgentItem agentItem = AgentItem.builder()
        .agent(agent)
        .advertiseCount(0)
        .showContactCount(0)
        .tickets(Set.of())
        .hasPurchasedAdvertise(false)

        .dailyFreeShowContactCount(10)
        .lastUseDaily(null)
        .build();
    return agentItemRepository.save(agentItem);
  }

  @Transactional
  public void applyPurchase(Member member, Map<PurchaseProduct, Integer> products) {

    AgentItem agentItem = this.getByAgent(member);

    for (Map.Entry<PurchaseProduct, Integer> entry : products.entrySet()) {
      PurchaseProduct product = entry.getKey();
      Integer amount = entry.getValue();

      ProductType type = product.getType();

      switch (type) {
        case TICKET:
          Integer days = product.getAmount();
          days = days * amount;

          Set<AgentTicket> tickets = agentItem.getTickets();
          AgentTicket currentTicket = this.getCurrentTicket(tickets);

          LocalDateTime ticketStartAt = LocalDateTime.now();
          if (currentTicket != null) {
            ticketStartAt = currentTicket.getTicketEndAt();
          }
          LocalDateTime ticketEndAt = ticketStartAt.plusDays(days);

          AgentTicket newTicket = AgentTicket.builder()
              .name(product.getName())
              .ticketStartAt(ticketStartAt)
              .ticketEndAt(ticketEndAt)
              .build();
          newTicket.setAgentItem(agentItem);

          agentTicketRepository.save(newTicket);
          break;
        case SHOW_CONTACT:
          Integer count = product.getAmount();
          count = count * amount;
          agentItem.setShowContactCount(agentItem.getShowContactCount() + count);
          break;
        case ADVERTISE:
          Integer advertiseCount = product.getAmount();
          advertiseCount = advertiseCount * amount;
          agentItem.setAdvertiseCount(agentItem.getAdvertiseCount() + advertiseCount);
          break;
      }
    }
  }

  public AgentTicket getCurrentTicket(Set<AgentTicket> tickets) {
    LocalDateTime today = LocalDateTime.now();
    return tickets.stream()
        .filter(ticket -> ticket.getTicketStartAt().isBefore(today) && ticket.getTicketEndAt()
            .isAfter(today))
        .findFirst()
        .orElse(null);
  }

  public AgentMyItemResp agentMyItemResp(AgentItem item) {

    AgentTicket currentTicket = this.getCurrentTicket(item.getTickets());

    String ticketName = null;
    LocalDateTime ticketStartAt = null;
    LocalDateTime ticketEndAt = null;

    if (currentTicket != null) {
      ticketName = currentTicket.getName();
      ticketStartAt = currentTicket.getTicketStartAt();
      ticketEndAt = currentTicket.getTicketEndAt();
    }

    return AgentMyItemResp.builder()
        .hasTicket(currentTicket != null)
        .ticketName(ticketName)
        .ticketStartAt(ticketStartAt)
        .ticketEndAt(ticketEndAt)
        .advertiseCount(item.getAdvertiseCount())
        .showContactCount(item.getShowContactCount())

        .dailyFreeShowContactCount(item.getDailyFreeShowContactCount())
        .build();
  }

  @Transactional
  public void save(AgentItem agentItem) {
    agentItemRepository.save(agentItem);
  }

  @Transactional
  public AgentTicket assignTicket(Member agent, String ticketName, LocalDateTime ticketStartAt, LocalDateTime ticketEndAt) {
    AgentItem agentItem = this.getByAgent(agent);

    // 현재 사용 중인 이용권 확인
    Set<AgentTicket> tickets = agentItem.getTickets();
    AgentTicket currentTicket = this.getCurrentTicket(tickets);

    // 현재 사용 중인 이용권이 있으면, 새 이용권 시작일을 조정
    LocalDateTime adjustedStartAt = ticketStartAt;
    LocalDateTime adjustedEndAt = ticketEndAt;

    if (currentTicket != null) {
      // 입력한 시작일이 현재 이용권이 끝나기 전이면, 현재 이용권 끝나는 날짜부터 시작
      if (ticketStartAt.isBefore(currentTicket.getTicketEndAt())) {
        adjustedStartAt = currentTicket.getTicketEndAt();
        // 기간을 유지하기 위해 종료일도 조정
        long daysBetween = java.time.Duration.between(ticketStartAt, ticketEndAt).toDays();
        adjustedEndAt = adjustedStartAt.plusDays(daysBetween);
      }
    }

    AgentTicket newTicket = AgentTicket.builder()
        .name(ticketName)
        .ticketStartAt(adjustedStartAt)
        .ticketEndAt(adjustedEndAt)
        .build();
    newTicket.setAgentItem(agentItem);

    return agentTicketRepository.save(newTicket);
  }

  @Transactional
  public void updateAgentItemCounts(Member agent, Integer showContactCount, Integer advertiseCount) {
    AgentItem agentItem = this.getByAgent(agent);

    if (showContactCount != null) {
      agentItem.setShowContactCount(showContactCount);
    }

    if (advertiseCount != null) {
      agentItem.setAdvertiseCount(advertiseCount);
    }

    agentItemRepository.save(agentItem);
  }
}
