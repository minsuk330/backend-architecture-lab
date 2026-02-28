package com.backend.lab.api.admin.promotionText.facade;


import com.backend.lab.api.admin.promotionText.dto.PromotionTextResp;
import com.backend.lab.common.entity.dto.resp.ListResp;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.admin.core.service.AdminService;
import com.backend.lab.domain.details.entity.Details;
import com.backend.lab.domain.details.service.DetailsService;
import com.backend.lab.domain.promotionText.entity.PromotionText;
import com.backend.lab.domain.promotionText.entity.PromotionTextField;
import com.backend.lab.domain.promotionText.entity.dto.req.PromotionTextUpdateReq;
import com.backend.lab.domain.promotionText.entity.dto.resp.PromotionTextFieldResp;
import com.backend.lab.domain.promotionText.entity.vo.PromotionMemberType;
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
public class AdminPromotionTextFacade {
  private final PromotionTextService promotionTextService;
  private final PropertyService propertyService;
  private final DetailsService detailsService;
  private final AdminService adminService;
  private final PromotionMapper promotionMapper;


  public ListResp<PromotionTextFieldResp> getPromotionText() {
    PromotionText promotionText = promotionTextService.ensureAdminPromotionExist();

    List<PromotionTextFieldResp> list = promotionText.getFields().stream().map(
        promotionTextService::promotionTextFieldResp
    ).toList();

    return new ListResp<>(list);
  }

  public void create(PromotionTextUpdateReq req) {
    promotionTextService.update(req, PromotionMemberType.ADMIN,null);
  }
  public ListResp<PromotionTextResp> getPromotionText(Long propertyId, Long userId) {
    Property property = propertyService.getByIdWithAllDetails(propertyId);

    if (userId==null) {
      return new ListResp<>(null);
    }

    Admin admin = adminService.getById(userId);

    Details details = detailsService.getByPropertyId(propertyId);
    //어드민
    if(admin!=null)  {
      PromotionText promotionText = promotionTextService.ensureAdminPromotionExist();
      List<PromotionTextField> fields = promotionText.getFields();
      System.out.println(promotionText.getFields().stream().map(text->toString()));

      List<PromotionTextResp> list = fields.stream().map(
              field -> promotionMapper.promotionTextResp(promotionMapper.placeholderToValue(field.getPlaceholder(), property,details), field))
          .toList();
      return new ListResp<>(list);
    }
    return new ListResp<>(null);

  }

}
