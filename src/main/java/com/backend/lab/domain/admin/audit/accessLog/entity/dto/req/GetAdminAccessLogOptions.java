package com.backend.lab.domain.admin.audit.accessLog.entity.dto.req;

import com.backend.lab.common.entity.dto.req.PageOptions;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
@Getter
@Setter
@NoArgsConstructor
public class GetAdminAccessLogOptions extends PageOptions {

  private List<Long> adminIds;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
}
