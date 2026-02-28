package com.backend.lab.api.admin.manageLog.facade;

import com.backend.lab.api.admin.manageLog.dto.MemberWorkLogDetailOptions;
import com.backend.lab.api.admin.manageLog.dto.MemberWorkLogDetailResp;
import com.backend.lab.api.admin.manageLog.dto.MemberWorkLogResp;
import com.backend.lab.api.admin.manageLog.dto.SearchMemberWorkLogOptions;
import com.backend.lab.common.entity.dto.resp.ListResp;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.member.memberWorkLog.entity.MemberWorkLog;
import com.backend.lab.domain.member.memberWorkLog.service.MemberWorkLogService;
import com.backend.lab.domain.property.propertyWorkLog.entity.PropertyWorkLog;
import com.backend.lab.domain.property.propertyWorkLog.entity.dto.req.PropertyWorkLogDetailOptions;
import com.backend.lab.domain.property.propertyWorkLog.entity.dto.req.PropertyWorkLogDetailsOptions;
import com.backend.lab.domain.property.propertyWorkLog.entity.dto.req.SearchPropertyWorkLogOptions;
import com.backend.lab.domain.property.propertyWorkLog.entity.dto.resp.PropertyWorkLogDetailResp;
import com.backend.lab.domain.property.propertyWorkLog.entity.dto.resp.PropertyWorkLogResp;
import com.backend.lab.domain.property.propertyWorkLog.service.PropertyWorkLogService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminManageLogFacade {

  private final PropertyWorkLogService propertyWorkLogService;
  private final MemberWorkLogService memberWorkLogService;


  public PageResp<PropertyWorkLogResp> search(SearchPropertyWorkLogOptions options) {
    Page<PropertyWorkLogResp> page = propertyWorkLogService.searchLog(options);

    List<PropertyWorkLogResp> list = page.getContent().stream().toList();

    return new PageResp<>(page, list);
  }


  public PageResp<PropertyWorkLogDetailResp> getDetails(PropertyWorkLogDetailOptions options) {
    Page<PropertyWorkLogDetailResp> page = propertyWorkLogService.findDetails(options);

    List<PropertyWorkLogDetailResp> list = page.getContent().stream().toList();

    return new PageResp<>(page, list);
  }


  public PageResp<PropertyWorkLogDetailResp> getAllDetails(PropertyWorkLogDetailsOptions options) {
    Page<PropertyWorkLogDetailResp> page = propertyWorkLogService.findAllDetails(options);

    List<PropertyWorkLogDetailResp> list = page.getContent().stream().toList();

    return new PageResp<>(page, list);
  }

  public PageResp<MemberWorkLogResp> gets(SearchMemberWorkLogOptions options) {
    Page<MemberWorkLogResp> page = memberWorkLogService.gets(options);

    List<MemberWorkLogResp> list = page.getContent().stream().toList();

    return new PageResp<>(page, list);
  }

  public PageResp<MemberWorkLogDetailResp> getDetails(MemberWorkLogDetailOptions options) {
    Page<MemberWorkLogDetailResp> page = memberWorkLogService.findDetails(options);

    List<MemberWorkLogDetailResp> list = page.getContent().stream().toList();

    return new PageResp<>(page, list);
  }

  public ListResp<PropertyWorkLogResp> getPropertyList() {
    List<PropertyWorkLog> list = propertyWorkLogService.gets();

    List<PropertyWorkLogResp> data = list.stream().map(
        propertyWorkLogService::propertyWorkLogResp
    ).toList();
    return new ListResp<>(data);
  }

  public ListResp<MemberWorkLogResp> getMemberList() {

    List<MemberWorkLog> list = memberWorkLogService.gets();

    List<MemberWorkLogResp> data = list.stream().map(
        memberWorkLogService::memberWorkLogResp
    ).toList();

    return new ListResp<>(data);
  }
}
