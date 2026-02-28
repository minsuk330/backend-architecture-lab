package com.backend.lab.domain.property.core.entity;

import com.backend.lab.common.entity.BaseEntity;
import com.backend.lab.domain.property.category.entity.Category;
import com.backend.lab.domain.property.core.entity.information.AddressInformation;
import com.backend.lab.domain.property.core.entity.information.FloorInformation;
import com.backend.lab.domain.property.core.entity.information.LandInformation;
import com.backend.lab.domain.property.core.entity.information.LedgeInformation;
import com.backend.lab.domain.property.core.entity.information.PriceInformation;
import com.backend.lab.domain.property.core.entity.information.RegisterInformation;
import com.backend.lab.domain.property.core.entity.information.TemplateInformation;
import com.backend.lab.domain.property.core.entity.vo.PropertyStatus;
import com.backend.lab.domain.uploadFile.entity.UploadFile;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "property")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@FilterDef(name = "deletedPropertyFilter", defaultCondition = "deleted_at IS NULL", autoEnabled = true)
@Filter(name = "deletedPropertyFilter")
@FilterDef(name = "onlyDeletedPropertyFilter", defaultCondition = "deleted_at IS NOT NULL", autoEnabled = false)
@Filter(name = "onlyDeletedPropertyFilter")
@FilterDef(name = "deletedPersistentPropertyFilter", defaultCondition = "delete_persistent_at IS NULL", autoEnabled = true)
@Filter(name = "deletedPersistentPropertyFilter")
@SQLDelete(sql = "UPDATE property SET deleted_at = NOW() WHERE id = ?")
public class Property extends BaseEntity {

  @Column(name = "delete_persistent_at", nullable = true)
  private LocalDateTime delete_persistent_at;

  private LocalDateTime confirmedAt;

  private Boolean isPublic; // 공개여부

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "thumbnail_image_id")
  private UploadFile thumbnailImageUrl; // 썸네일 이미지 URL

  @Builder.Default
  @Column(nullable = false)
  private Long wishCount = 0L; //관심 카운트

  private String buildingName; // 건물명
  @Enumerated(EnumType.STRING)
  private PropertyStatus status; // 진행상태

  private LocalDate completedAt; // 완료일
  private LocalDate pendingAt; // 보류일

  private Long adminId; // 담당자 id

  @Column(name = "agent_id")
  private Long exclusiveAgentId; //전속

  @JoinColumn(name = "big_category_id")
  @ManyToOne(fetch = FetchType.EAGER)
  private Category bigCategory;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "property_small_category",
      joinColumns = @JoinColumn(name = "property_id"),
      inverseJoinColumns = @JoinColumn(name = "category_id")
  )
  @Builder.Default
  @BatchSize(size = 100)
  private Set<Category> smallCategories = new LinkedHashSet<>();

  @OneToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "property_floor_information",
      joinColumns = @JoinColumn(name = "property_id"),
      inverseJoinColumns = @JoinColumn(name = "floor_information_id")
  )
  @Builder.Default
  @BatchSize(size = 100)
  @OrderBy("buildingOrder ASC")
  private Set<FloorInformation> floors = new LinkedHashSet<>(); // 층 정보

  @JoinColumn(name = "address_information_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private AddressInformation address;


  @Builder.Default
  @BatchSize(size = 100)
  @OrderBy("buildingOrder ASC")
  @OneToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "property_land_information",
      joinColumns = @JoinColumn(name = "property_id"),
      inverseJoinColumns = @JoinColumn(name = "land_information_id")
  )
  private List<LandInformation> land = new ArrayList<>();

  @OneToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "property_ledge_information",
      joinColumns = @JoinColumn(name = "property_id"),
      inverseJoinColumns = @JoinColumn(name = "ledge_information_id")
  )
  @Builder.Default
  @BatchSize(size = 100)
  @OrderBy("buildingOrder ASC")
  private List<LedgeInformation> ledge = new ArrayList<>();

  @JoinColumn(name = "price_information_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private PriceInformation price;

  @JoinColumn(name = "register_information_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private RegisterInformation register;

  @JoinColumn(name = "template_information_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private TemplateInformation template;
}
