package com.backend.lab.domain.member.memberWorkLog.repository;

import com.backend.lab.api.admin.manageLog.dto.MemberWorkLogDetailOptions;
import com.backend.lab.api.admin.manageLog.dto.MemberWorkLogDetailResp;
import com.backend.lab.api.admin.manageLog.dto.MemberWorkLogResp;
import com.backend.lab.api.admin.manageLog.dto.SearchMemberWorkLogOptions;
import org.springframework.data.domain.Page;

public interface MemberWorkLogRepositoryCustom {

  Page<MemberWorkLogResp> gets(SearchMemberWorkLogOptions options);

  Page<MemberWorkLogDetailResp> findDetails(MemberWorkLogDetailOptions options);
}
