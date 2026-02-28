package com.backend.lab.api.admin.property.core.dto.resp;

import com.backend.lab.common.entity.dto.resp.BaseResp;
import com.backend.lab.domain.details.entity.dto.resp.DetailsResp;
import com.backend.lab.domain.property.core.entity.embedded.AddressProperties;
import com.backend.lab.domain.property.core.entity.embedded.LandProperties;
import com.backend.lab.domain.property.core.entity.embedded.LedgerProperties;
import com.backend.lab.domain.property.core.entity.embedded.PriceProperties;
import com.backend.lab.domain.property.core.entity.embedded.RegisterProperties;
import com.backend.lab.domain.property.taskNote.entity.dto.resp.TaskNoteResp;
import com.backend.lab.domain.secret.entity.dto.resp.SecretResp;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class PropertyUpdateResp extends BaseResp {

  private List<LandProperties> lands;
  //건축물대장
  private List<LedgerProperties> ledges;
  //업무일지
  private List<TaskNoteResp> taskNote;
  //등기부등본정보
  private RegisterProperties register;
  //템플릿
  private PropertyTemplateResp template;
  //비밀메모
  private List<SecretResp> secret;
  //상세정보
  private DetailsResp detail;
  //금액정보
  private PriceProperties price;
  //층별
  private List<BuildingLeaseResp> leaseStatus;
  //매도자 정보
  private List<CustomerUpdatePropertyResp> members;
  //기본 정보
  private PropertyDefaultResp defaults;
  //위치 정보
  private AddressProperties address;

}
