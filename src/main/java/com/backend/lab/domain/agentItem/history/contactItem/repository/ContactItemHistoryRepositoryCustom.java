package com.backend.lab.domain.agentItem.history.contactItem.repository;

import com.backend.lab.domain.agentItem.history.contactItem.entity.ContactItemHistory;
import com.backend.lab.domain.agentItem.history.contactItem.entity.dto.ContactHistorySearchOptions;
import org.springframework.data.domain.Page;

public interface ContactItemHistoryRepositoryCustom {

  Page<ContactItemHistory> search(ContactHistorySearchOptions options);
}
