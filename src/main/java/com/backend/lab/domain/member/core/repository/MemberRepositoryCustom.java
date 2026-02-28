package com.backend.lab.domain.member.core.repository;

import com.backend.lab.api.admin.itemHistory.contactItem.dto.req.AdminContactCountSearchOptions;
import com.backend.lab.api.admin.itemHistory.contactItem.dto.resp.AdminContactCountResp;
import com.backend.lab.api.admin.member.agent.facade.dto.req.AdminAgentListOptions;
import com.backend.lab.api.admin.member.agent.facade.dto.req.AdminAgentSearchOptions;
import com.backend.lab.api.admin.member.buyer.facade.dto.req.AdminBuyerSearchOptions;
import com.backend.lab.api.admin.member.seller.facade.dto.req.AdminSellerSearchOptions;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.entity.dto.req.AdminCustomerSearchOptions;
import com.backend.lab.domain.member.core.entity.dto.req.SearchAgentOptions;
import com.backend.lab.domain.member.core.entity.dto.req.SearchSellerAndCustomerOptions;
import org.springframework.data.domain.Page;

public interface MemberRepositoryCustom {

  Page<Member> searchSellerAndCustomer(SearchSellerAndCustomerOptions options);

  Page<Member> searchAgent(SearchAgentOptions options);

  Page<Member> searchCustomer(AdminCustomerSearchOptions options);

  Page<Member> adminSearchAgent(AdminAgentSearchOptions options);

  Page<Member> adminSearchBuyer(AdminBuyerSearchOptions options);

  Page<Member> adminSearchSeller(AdminSellerSearchOptions options);

  Page<AdminContactCountResp> searchAgentWithCount(AdminContactCountSearchOptions options);

  Page<Member> listActiveAgents(AdminAgentListOptions options);
}
