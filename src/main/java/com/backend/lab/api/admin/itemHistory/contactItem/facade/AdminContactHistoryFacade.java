package com.backend.lab.api.admin.itemHistory.contactItem.facade;

import com.backend.lab.api.admin.itemHistory.contactItem.dto.req.AdminContactCountSearchOptions;
import com.backend.lab.api.admin.itemHistory.contactItem.dto.resp.AdminContactCountResp;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.agentItem.history.contactItem.entity.ContactItemHistory;
import com.backend.lab.domain.agentItem.history.contactItem.entity.dto.ContactHistorySearchOptions;
import com.backend.lab.domain.agentItem.history.contactItem.entity.dto.ContactItemHistoryResp;
import com.backend.lab.domain.agentItem.history.contactItem.service.ContactItemHistoryService;
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
public class AdminContactHistoryFacade {

  private final ContactItemHistoryService contactItemHistoryService;
  private final AgentService agentService;

  public PageResp<ContactItemHistoryResp> search(ContactHistorySearchOptions options) {
    Page<ContactItemHistory> page = contactItemHistoryService.search(options);
    List<ContactItemHistoryResp> data = page.getContent().stream()
        .sorted(Comparator.nullsLast(Comparator.comparing(ContactItemHistory::getCreatedAt).reversed()))
        .filter(Objects::nonNull)
        .map(contactItemHistoryService::itemHistoryResp)
        .toList();
    return new PageResp<>(page, data);
  }

  public PageResp<AdminContactCountResp> count(AdminContactCountSearchOptions options) {
    Page<AdminContactCountResp> page =  agentService.searchWithCount(options);
    List<AdminContactCountResp> data = page.getContent();
    return new PageResp<>(page, data);
  }
}
