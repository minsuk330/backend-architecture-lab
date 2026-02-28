package com.backend.lab.api.admin.property.core.dto.req;

import com.backend.lab.domain.details.entity.dto.req.DetailReq;
import com.backend.lab.domain.property.core.entity.embedded.AddressProperties;
import com.backend.lab.domain.property.core.entity.embedded.LandProperties;
import com.backend.lab.domain.property.core.entity.embedded.LedgerProperties;
import com.backend.lab.domain.property.core.entity.embedded.PriceProperties;
import com.backend.lab.domain.property.core.entity.embedded.RegisterProperties;
import com.backend.lab.domain.property.taskNote.entity.dto.req.TaskNoteReq;
import com.backend.lab.domain.secret.entity.dto.req.SecretReq;
import java.util.List;
import lombok.Getter;

@Getter
public class PropertyUpdateReq {
  //토지정보
  private List<LandProperties> lands;
  //건축물대장
  private List<LedgerProperties> ledges;
  //업무일지
  private TaskNoteReq taskNote;
  //등기부등본정보
  private RegisterProperties register;
  //템플릿
  private PropertyTemplateReq template;
  //비밀메모
  private SecretReq secret;
  //상세정보
  private DetailReq detail;
  //금액정보
  private PriceProperties price;
  //층별
  private List<PropertyFloorReq> floors;
  //매도자 정보
  private List<PropertyCustomerCreateReq> members;
  //기본 정보
  private PropertyDefaultReq defaults;
  //위치 정보
  private AddressProperties address;
}
