package com.backend.lab.domain.property.category.service;

import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import com.backend.lab.domain.property.category.entity.Category;
import com.backend.lab.domain.property.category.entity.dto.req.BigCategoryDTO;
import com.backend.lab.domain.property.category.entity.dto.req.CategoryRerankReq;
import com.backend.lab.domain.property.category.entity.dto.req.SmallCategoryDTO;
import com.backend.lab.domain.property.category.entity.dto.resp.BigCategoryResp;
import com.backend.lab.domain.property.category.entity.dto.resp.SmallCategoryResp;
import com.backend.lab.domain.property.category.entity.vo.CategoryType;
import com.backend.lab.domain.property.category.repository.CategoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

  private final CategoryRepository categoryRepository;

  public List<Category> getsByType(CategoryType type) {
    return categoryRepository.getCategoriesByType(type);
  }

  public List<Category> gets(List<Long> ids) {
    return categoryRepository.findAllById(ids);
  }

  public Category getById(Long id) {
    return categoryRepository.findById(id).orElseThrow(
        () -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "분류")
    );
  }

  @Transactional
  public void rerank(CategoryRerankReq req) {
    List<Long> categoryIds = req.getIds();
    List<Category> categories = this.gets(categoryIds);
    for (int i = 0; i < categoryIds.size(); i++) {
      Long id = categoryIds.get(i);
      Category category = categories.stream()
          .filter(c -> c.getId().equals(id))
          .findFirst()
          .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "대분류"));
      category.setRank(i * 10);
    }
  }

  @Transactional
  public Category createBig(BigCategoryDTO dto) {
    return categoryRepository.save(
        Category.builder()
            .isActive(dto.getIsActive())
            .type(CategoryType.BIG)
            .name(dto.getName())
            .rank(dto.getRank())
            .menuTypes(dto.getMenuTypes())
            .migrationCategoryId(dto.getMigrationCategoryId())
            .build()
    );
  }

  @Transactional
  public Category updateBig(Long id, BigCategoryDTO dto) {
    Category category = this.getById(id);
    category.setIsActive(dto.getIsActive());
    category.setName(dto.getName());
    category.setRank(dto.getRank());
    category.setMenuTypes(dto.getMenuTypes());
    category.setMigrationCategoryId(dto.getMigrationCategoryId());
    return categoryRepository.save(category);
  }

  @Transactional
  public Category createSmall(SmallCategoryDTO dto) {
    return categoryRepository.save(
        Category.builder()
            .type(CategoryType.SMALL)
            .name(dto.getName())
            .rank(dto.getRank())
            .migrationCategoryId(dto.getMigrationCategoryId())
            .build()
    );
  }

  @Transactional
  public Category updateSmall(Long id, SmallCategoryDTO dto) {
    Category category = this.getById(id);
    category.setName(dto.getName());
    category.setRank(dto.getRank());
    category.setMigrationCategoryId(dto.getMigrationCategoryId());
    return categoryRepository.save(category);
  }

  @Transactional
  public void delete(Long id) {
    Category category = this.getById(id);
    categoryRepository.delete(category);
  }

  public BigCategoryResp bigCategoryResp(Category category) {

    if (category == null) {
      return null;
    }

    Long migrationCategoryId = category.getMigrationCategoryId();
    String mgName = null;
    if (migrationCategoryId != null) {
      Category mg = this.getById(migrationCategoryId);
      mgName = mg.getName();
    }


    return BigCategoryResp.builder()
        .id(category.getId())
        .isActive(category.getIsActive())
        .rank(category.getRank())
        .name(category.getName())
        .menuTypes(category.getMenuTypes())
        .migrationCategoryId(migrationCategoryId)
        .migrationCategoryName(mgName)
        .build();
  }

  public SmallCategoryResp smallCategoryResp(Category category) {

    if (category == null) {
      return null;
    }


    Long migrationCategoryId = category.getMigrationCategoryId();
    String mgName = null;
    if (migrationCategoryId != null) {
      Category mg = this.getById(migrationCategoryId);
      mgName = mg.getName();
    }

    return SmallCategoryResp.builder()
        .id(category.getId())
        .rank(category.getRank())
        .name(category.getName())
        .migrationCategoryId(migrationCategoryId)
        .migrationCategoryName(mgName)
        .build();
  }

  public Category findByName(String categoryName) {
    return categoryRepository.findByName(categoryName);
  }
}
