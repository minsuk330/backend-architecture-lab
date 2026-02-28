package com.backend.lab.api.member.agent.conatctItem.facade;

import com.backend.lab.common.entity.dto.req.PageOptions;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.agentItem.core.entity.AgentItem;
import com.backend.lab.domain.agentItem.core.entity.vo.AgentTicketStatus;
import com.backend.lab.domain.agentItem.core.service.AgentItemService;
import com.backend.lab.domain.agentItem.history.contactItem.entity.ContactItemHistory;
import com.backend.lab.domain.agentItem.history.contactItem.entity.dto.ContactHistorySearchOptions;
import com.backend.lab.domain.agentItem.history.contactItem.entity.dto.ContactItemHistoryResp;
import com.backend.lab.domain.agentItem.history.contactItem.entity.dto.ContactItemHistoryResp.ContactHistoryCustomer;
import com.backend.lab.domain.agentItem.history.contactItem.service.ContactItemHistoryService;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.service.AgentService;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AgentContactItemFacade {

  private final AgentService agentService;
  private final AgentItemService agentItemService;
  private final ContactItemHistoryService contactItemHistoryService;

  public PageResp<ContactItemHistoryResp> gets(PageOptions options, Long userId) {
    ContactHistorySearchOptions searchOptions = new ContactHistorySearchOptions();
    searchOptions.setPage(options.getPage());
    searchOptions.setSize(options.getSize());
    searchOptions.setAgentId(userId);
    Page<ContactItemHistory> page = contactItemHistoryService.search(searchOptions);
    List<ContactItemHistoryResp> data = page.getContent().stream()
        .filter(Objects::nonNull)
        .sorted(Comparator.nullsLast(Comparator.comparing(ContactItemHistory::getCreatedAt).reversed()))
        .map(contactItemHistoryService::itemHistoryResp)
        .toList();

    Member agent = agentService.getById(userId);
    AgentItem item = agentItemService.getByAgent(agent);
    AgentTicketStatus status = agentItemService.getTicketStatus(item);
    if (!status.equals(AgentTicketStatus.USING)) {
      data = this.filter(data);
    }

    return new PageResp<>(page, data);
  }

  public List<ContactItemHistoryResp> filter(List<ContactItemHistoryResp> original) {
    return original.stream()
        .map(
            item -> {
              ContactItemHistoryResp newItem = new ContactItemHistoryResp(item);

              for (ContactHistoryCustomer customer : newItem.getCustomers()) {
                String hideName = customer.getName();
                if (hideName != null && hideName.length() > 1) {
                  hideName = hideName.charAt(0) + "*".repeat(hideName.length() - 1);
                }
                customer.setName(hideName);

                String hidePhoneNumber = customer.getPhoneNumber();
                if (hidePhoneNumber != null && hidePhoneNumber.length() > 3) {
                  hidePhoneNumber =
                      hidePhoneNumber.substring(0, 3) + "*".repeat(hidePhoneNumber.length() - 3);
                }
                customer.setPhoneNumber(hidePhoneNumber);
              }

              return newItem;
            }
        )
        .toList();
  }
}
