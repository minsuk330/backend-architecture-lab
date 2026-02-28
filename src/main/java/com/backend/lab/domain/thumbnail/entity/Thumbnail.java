package com.backend.lab.domain.thumbnail.entity;

import com.backend.lab.common.entity.BaseEntity;
import com.backend.lab.domain.thumbnail.entity.vo.ThumbnailType;
import com.backend.lab.domain.uploadFile.entity.UploadFile;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
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
public class Thumbnail extends BaseEntity {

  private Long agentId;

  @Enumerated(EnumType.STRING)
  private ThumbnailType thumbnailType;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinTable(
      name = "thumbnail_images",
      joinColumns = @JoinColumn(name = "thumbnail_id"),
      inverseJoinColumns = @JoinColumn(name = "upload_file_id")
  )
  @OrderColumn(name = "thumbnail_order")
  @Builder.Default
  private List<UploadFile> thumbnails = new ArrayList<>();


  public UploadFile getMainThumbnail() {
    return thumbnails.isEmpty() ? null : thumbnails.get(0);
  }

  public boolean hasMainThumbnail() {
    return !thumbnails.isEmpty();
  }
  //대표 이미지 세팅
  public void setMainThumbnail(UploadFile thumbnail) {
    if (thumbnail == null || !thumbnails.contains(thumbnail)) {
      return;
    }

    if(thumbnails.get(0).equals(thumbnail)) {
      return;
    }
    thumbnails.remove(thumbnail);
    thumbnails.add(0, thumbnail);

  }

}
