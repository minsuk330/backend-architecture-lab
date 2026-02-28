package com.backend.lab.api.admin.manageLog.dto;

import com.backend.lab.common.entity.dto.req.PageOptions;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
@Getter
@Setter
@NoArgsConstructor
public class SearchMemberWorkLogOptions extends PageOptions {

}
