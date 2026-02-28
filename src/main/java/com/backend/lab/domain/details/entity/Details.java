package com.backend.lab.domain.details.entity;

import com.backend.lab.common.entity.BaseEntity;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.uploadFile.entity.UploadFile;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Details extends BaseEntity {

  //상세 설명
  //길이 2000
  @Column(length = 2000)
  private String content;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "property_id")
  private Property property;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinTable(
      name = "details_property_images",
      joinColumns = @JoinColumn(name = "details_id"),
      inverseJoinColumns = @JoinColumn(name = "upload_file_id")
  )
  @Builder.Default
  @Size(max = 20)
  private List<UploadFile> propertyImages = new ArrayList<>();

  @OneToMany(cascade = CascadeType.ALL)
  @JoinTable(
      name = "details_data_images",
      joinColumns = @JoinColumn(name = "details_id"),
      inverseJoinColumns = @JoinColumn(name = "upload_file_id")
  )
  @Builder.Default
  @Size(max = 10)
  private List<UploadFile> dataImages = new ArrayList<>();

  @Column(length = 999)
  private String etc;

}
