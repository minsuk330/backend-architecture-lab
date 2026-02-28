package com.backend.lab.domain.popup.entity.dto.req;

import com.backend.lab.common.entity.dto.req.PageOptions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchPopupOptions extends PageOptions {

  private String adminName; // 관리자 이름
}
