package com.backend.lab.api.member.common.property.promotionText.facade;

import com.backend.lab.api.admin.promotionText.dto.PromotionTextResp;
import com.backend.lab.api.admin.promotionText.facade.PromotionMapper;
import com.backend.lab.common.entity.dto.resp.ListResp;
import com.backend.lab.domain.details.entity.Details;
import com.backend.lab.domain.details.service.DetailsService;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.entity.vo.MemberType;
import com.backend.lab.domain.member.core.service.MemberService;
import com.backend.lab.domain.promotionText.entity.PromotionText;
import com.backend.lab.domain.promotionText.entity.PromotionTextField;
import com.backend.lab.domain.promotionText.service.PromotionTextService;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.property.core.service.PropertyService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberPromotionFacade {

  private final PromotionMapper promotionMapper;
  private final PromotionTextService promotionTextService;
  private final PropertyService propertyService;
  private final MemberService memberService;
  private final DetailsService detailsService;

  public ListResp<PromotionTextResp> getPromotionText(Long propertyId, Long userId) {
    Property property = propertyService.getByIdWithAllDetails(propertyId);
    //비회원
    if (userId==null) {
      return new ListResp<>(null);
    }

    Member member = memberService.getById(userId);

    Details details = detailsService.getByPropertyId(propertyId);
    //공인
    if (member!=null&&member.getType() == MemberType.AGENT) {
      PromotionText promotionText = promotionTextService.ensureAgentPromotionExist(
          userId);
      List<PromotionTextField> fields = promotionText.getFields();
      List<PromotionTextResp> list = fields.stream().map(
              field -> promotionMapper.promotionTextResp(promotionMapper.placeholderToValue(field.getPlaceholder(), property,details), field))
          .toList();
      return new ListResp<>(list);

    }
    return new ListResp<>(null);

  }


}
