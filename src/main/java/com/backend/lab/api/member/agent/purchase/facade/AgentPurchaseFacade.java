package com.backend.lab.api.member.agent.purchase.facade;

import com.backend.lab.common.entity.dto.req.PageOptions;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.service.AgentService;
import com.backend.lab.domain.purchase.core.entity.PurchaseItem;
import com.backend.lab.domain.purchase.core.entity.dto.PurchaseResp;
import com.backend.lab.domain.purchase.core.service.PurchaseService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AgentPurchaseFacade {

  private final AgentService agentService;
  private final PurchaseService purchaseService;

  public PageResp<PurchaseResp> gets(Long agentId, PageOptions pageOptions) {
    Member agent = agentService.getById(agentId);
    Page<PurchaseItem> page = purchaseService.getItemsByAgent(agent, pageOptions.pageable());
    List<PurchaseResp> data = page.getContent().stream()
        .map(p -> purchaseService.purchaseResp(p, agent.getAgentProperties().getName(), agentId))
        .toList();
    return new PageResp<>(page, data);
  }
}
