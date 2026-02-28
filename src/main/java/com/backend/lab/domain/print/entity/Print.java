package com.backend.lab.domain.print.entity;

import com.backend.lab.common.entity.BaseEntity;
import com.backend.lab.domain.print.entity.vo.PrintType;
import com.backend.lab.domain.uploadFile.entity.UploadFile;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Print extends BaseEntity {

  //어떤게 대표 이미지인지도 설정해야함
  private Long agentId;

  @Enumerated(EnumType.STRING)
  private PrintType printType;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinTable(
      name = "print_first_page_images",
      joinColumns = @JoinColumn(name = "print_id"),
      inverseJoinColumns = @JoinColumn(name = "upload_file_id")
  )
  @OrderColumn(name = "first_image_order")
  @Size(max = 5)
  @Builder.Default
  private List<UploadFile> firstPageImages = new ArrayList<>();

  @OneToMany(cascade = CascadeType.ALL)
  @JoinTable(
      name = "print_last_page_images",
      joinColumns = @JoinColumn(name = "print_id"),
      inverseJoinColumns = @JoinColumn(name = "upload_file_id")
  )
  @OrderColumn(name = "last_image_order")
  @Size(max = 5)
  @Builder.Default
  private List<UploadFile> lastPageImages = new ArrayList<>();

  private String lastPageMainTitle;   // 메인 제목
  @Column(length = 7)
  private String lastPageMainColor;

  private String lastPageSubTitle;    // 서브 제목
  @Column(length = 7)
  private String lastPageSubColor;

}
