package com.backend.lab.domain.member.core.repository;

import com.backend.lab.domain.member.core.entity.DeletedMember;
import com.backend.lab.domain.member.core.entity.dto.req.AdminCustomerSearchOptions;
import org.springframework.data.domain.Page;

public interface DeletedMemberRepositoryCustom {

  Page<DeletedMember> searchCustomer(AdminCustomerSearchOptions options);
}
