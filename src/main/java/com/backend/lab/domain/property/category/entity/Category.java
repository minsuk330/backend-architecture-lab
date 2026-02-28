package com.backend.lab.domain.property.category.entity;

import com.backend.lab.common.entity.BaseEntity;
import com.backend.lab.domain.property.category.entity.vo.CategoryType;
import com.backend.lab.domain.property.category.entity.vo.MenuType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "category")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Category extends BaseEntity {

    private Boolean isActive;

    private Integer rank;
    private String name;

    @ElementCollection(targetClass = MenuType.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "category_menu_type", joinColumns = @JoinColumn(name = "category_id"))
    @Enumerated(EnumType.STRING)
    private Set<MenuType> menuTypes = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private CategoryType type;

    private Long migrationCategoryId;
}
