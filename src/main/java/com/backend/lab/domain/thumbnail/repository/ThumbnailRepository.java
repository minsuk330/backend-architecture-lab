package com.backend.lab.domain.thumbnail.repository;

import com.backend.lab.domain.thumbnail.entity.Thumbnail;
import com.backend.lab.domain.thumbnail.entity.vo.ThumbnailType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThumbnailRepository extends JpaRepository<Thumbnail,Long> {


  Optional<Thumbnail> findByThumbnailTypeAndAgentId(ThumbnailType thumbnailType, Long agentId);

  Optional<Thumbnail> findByThumbnailTypeAndAgentIdIsNull(ThumbnailType thumbnailType);
}
