package com.backend.lab.domain.property.category.repository;

import com.backend.lab.domain.property.category.entity.Category;
import com.backend.lab.domain.property.category.entity.vo.CategoryType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category, Long> {

  @Query("select c from Category c where c.type = :type")
  List<Category> getCategoriesByType(@Param("type") CategoryType type);

  @Query("select c from Category c where c.name = :categoryName")
  Category findByName(@Param("categoryName") String categoryName);
}
