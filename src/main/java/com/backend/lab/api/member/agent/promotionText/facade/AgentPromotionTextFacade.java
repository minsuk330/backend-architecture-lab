package com.backend.lab.api.member.agent.promotionText.facade;

import com.backend.lab.common.entity.dto.resp.ListResp;
import com.backend.lab.domain.promotionText.entity.PromotionText;
import com.backend.lab.domain.promotionText.entity.dto.req.PromotionTextUpdateReq;
import com.backend.lab.domain.promotionText.entity.dto.resp.PromotionTextFieldResp;
import com.backend.lab.domain.promotionText.entity.vo.PromotionMemberType;
import com.backend.lab.domain.promotionText.service.PromotionTextService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AgentPromotionTextFacade {
  private final PromotionTextService promotionTextService;

  public ListResp<PromotionTextFieldResp> getPromotionText(Long agentId) {
    PromotionText promotionText = promotionTextService.ensureAgentPromotionExist(agentId);

    List<PromotionTextFieldResp> list = promotionText.getFields().stream().map(
        promotionTextService::promotionTextFieldResp
    ).toList();

    return new ListResp<>(list);
  }

  public void create(PromotionTextUpdateReq req, Long agentId) {
    promotionTextService.update(req, PromotionMemberType.AGENT,agentId);
  }
}
