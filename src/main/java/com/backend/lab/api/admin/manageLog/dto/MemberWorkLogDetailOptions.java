package com.backend.lab.api.admin.manageLog.dto;

import com.backend.lab.common.entity.dto.req.PageOptions;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberWorkLogDetailOptions extends PageOptions {

  private Long workLogId;

}
