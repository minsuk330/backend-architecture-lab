package com.backend.lab.api.member.agent.payment.facade.item;

import com.backend.lab.api.admin.member.agent.facade.dto.resp.AdminAgentTicketsResp;
import com.backend.lab.api.member.agent.payment.facade.item.dto.AgentMyItemResp;
import com.backend.lab.common.entity.dto.req.PageOptions;
import com.backend.lab.common.entity.dto.resp.ListResp;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.agentItem.core.entity.AgentItem;
import com.backend.lab.domain.agentItem.core.entity.AgentTicket;
import com.backend.lab.domain.agentItem.core.service.AgentItemService;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.service.AgentService;
import com.backend.lab.domain.purchase.core.service.PurchaseService;
import com.backend.lab.domain.purchase.item.entity.PurchaseProduct;
import com.backend.lab.domain.purchase.item.entity.dto.ProductResp;
import com.backend.lab.domain.purchase.item.entity.vo.ProductType;
import com.backend.lab.domain.purchase.item.service.PurchaseProductService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AgentItemFacade {

  private final AgentService agentService;
  private final AgentItemService agentItemService;
  private final PurchaseService purchaseService;
  private final PurchaseProductService productService;

  @Transactional
  public AgentMyItemResp getMyItem(Long agentId) {
    Member agent = agentService.getById(agentId);
    AgentItem item = agentItemService.getByAgent(agent);

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime lastUseDaily = item.getLastUseDaily();
    if (lastUseDaily != null) {

      LocalDate nowDate = now.toLocalDate();
      LocalDate lastUseDailyLocalDate = lastUseDaily.toLocalDate();
      if (nowDate.isAfter(lastUseDailyLocalDate)) {
        item.setDailyFreeShowContactCount(10);
      }
    } else {
      item.setDailyFreeShowContactCount(10);
    }

    return agentItemService.agentMyItemResp(item);
  }

  public ListResp<ProductResp> getProducts(Long agentId) {
    Member agent = agentService.getById(agentId);

    List<PurchaseProduct> products = productService.gets();
    boolean canPurchase = purchaseService.hasPurchasedAdvertise(agent);
    List<ProductResp> productResps = new ArrayList<>();
    for (PurchaseProduct product : products) {

      boolean cp = true;
      // 추가 광고건
      if (product.getType().equals(ProductType.ADVERTISE) && product.getAmount() == 10) {
        cp = canPurchase;
      }

      productResps.add(
          ProductResp.builder()
              .id(product.getId())

              .type(product.getType())
              .name(product.getName())
              .price(product.getPrice())
              .amount(product.getAmount())
              .canPurchase(cp)
              .build()
      );
    }
    return new ListResp<>(productResps);
  }

  public PageResp<AdminAgentTicketsResp> getTickets(Long userId, PageOptions options) {
    Member agent = agentService.getById(userId);
    AgentItem agentItem = agentItemService.getByAgent(agent);
    Set<AgentTicket> tickets = agentItem.getTickets();

    Pageable pageable = options.pageable();
    int page = pageable.getPageNumber();
    int size = pageable.getPageSize();

    List<AdminAgentTicketsResp> data = tickets.stream()
        .filter(Objects::nonNull)
        .sorted(Comparator.nullsLast(Comparator.comparing(AgentTicket::getTicketStartAt)))
        .sorted(Comparator.nullsLast(Comparator.comparing(AgentTicket::getTicketEndAt)))
        .skip((long) page * size)
        .limit(size)
        .map(at -> AdminAgentTicketsResp.builder()
            .name(at.getName())
            .ticketStartAt(at.getTicketStartAt())
            .ticketEndAt(at.getTicketEndAt())
            .status(agentItemService.getTicketStatus(at))
            .build())
        .toList();

    long total = (long) tickets.size();
    int totalPage = data.size() / size;

    return new PageResp<>(page, size,totalPage,total,data);
  }
}
