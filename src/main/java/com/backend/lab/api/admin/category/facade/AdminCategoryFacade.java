package com.backend.lab.api.admin.category.facade;

import com.backend.lab.common.entity.dto.resp.ListResp;
import com.backend.lab.domain.property.category.entity.Category;
import com.backend.lab.domain.property.category.entity.dto.req.BigCategoryDTO;
import com.backend.lab.domain.property.category.entity.dto.req.CategoryRerankReq;
import com.backend.lab.domain.property.category.entity.dto.req.SmallCategoryDTO;
import com.backend.lab.domain.property.category.entity.dto.resp.BigCategoryResp;
import com.backend.lab.domain.property.category.entity.dto.resp.SmallCategoryResp;
import com.backend.lab.domain.property.category.entity.vo.CategoryType;
import com.backend.lab.domain.property.category.service.CategoryService;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.property.core.service.PropertyService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCategoryFacade {

  private final CategoryService categoryService;
  private final PropertyService propertyService;

  public ListResp<BigCategoryResp> getBigs() {
    List<BigCategoryResp> data = categoryService.getsByType(CategoryType.BIG).stream()
        .sorted((c1, c2) -> c1.getRank() - c2.getRank())
        .map(categoryService::bigCategoryResp)
        .toList();
    return new ListResp<>(data);
  }

  @Transactional
  public void createBig(BigCategoryDTO dto) {
    categoryService.createBig(dto);
  }

  @Transactional
  public void rerankBig(CategoryRerankReq req) {
    categoryService.rerank(req);
  }

  @Transactional
  public void updateBig(Long id, BigCategoryDTO dto) {
    categoryService.updateBig(id, dto);
  }

  @Transactional
  public void deleteBig(Long id) {
    List<Property> properties = propertyService.getsByBigCategoryId(id);
    Category category = categoryService.getById(id);

    Category next = null;
    Long nextId = category.getMigrationCategoryId();

    if (nextId != null) {
      next = categoryService.getById(nextId);
    }

    for (Property property : properties) {
      property.setBigCategory(next);
    }

    categoryService.delete(id);
  }

  public ListResp<SmallCategoryResp> getSmalls() {
    List<SmallCategoryResp> data = categoryService.getsByType(CategoryType.SMALL).stream()
        .sorted((c1, c2) -> c1.getRank() - c2.getRank())
        .map(categoryService::smallCategoryResp)
        .toList();
    return new ListResp<>(data);
  }

  @Transactional
  public void createSmall(SmallCategoryDTO dto) {
    categoryService.createSmall(dto);
  }

  @Transactional
  public void rerankSmall(CategoryRerankReq req) {
    categoryService.rerank(req);
  }

  @Transactional
  public void updateSmall(Long id, SmallCategoryDTO dto) {
    categoryService.updateSmall(id, dto);
  }

  @Transactional
  public void deleteSmall(Long id) {
    List<Property> properties = propertyService.getsBySmallCategoryId(id);
    Category category = categoryService.getById(id);

    Category next = null;
    Long nextId = category.getMigrationCategoryId();

    if (nextId != null) {
      next = categoryService.getById(nextId);
    }

    for (Property property : properties) {
      property.getSmallCategories().removeIf(cat -> cat.getId().equals(id));
      if (next != null) {
        property.getSmallCategories().add(next);
      }
    }

    categoryService.delete(id);
  }
}
