package com.backend.lab.domain.thumbnail.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.backend.lab.domain.thumbnail.entity.Thumbnail;
import com.backend.lab.domain.thumbnail.entity.dto.req.ThumbnailReq;
import com.backend.lab.domain.thumbnail.entity.vo.ThumbnailType;
import com.backend.lab.domain.thumbnail.repository.ThumbnailRepository;
import com.backend.lab.domain.uploadFile.entity.UploadFile;
import com.backend.lab.domain.uploadFile.repository.UploadFileRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)  // ✅ 추가
@SpringBootTest
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.show-sql=true"
})

class ThumbnailServiceTest {
  @Autowired
  private ThumbnailService thumbnailService;
  @Autowired
  private ThumbnailRepository thumbnailRepository;
  @Autowired
  private UploadFileRepository uploadFileRepository;

  private UploadFile uploadFile1;
  private UploadFile uploadFile2;
  private UploadFile uploadFile3;

  @BeforeEach
  void setUp() {
    // 테스트용 UploadFile 생성
    uploadFile1 = UploadFile.builder()
        .fileName("image1.jpg")
        .fileUrl("https://test.com/image1.jpg")
        .build();

    uploadFile2 = UploadFile.builder()
        .fileName("image2.jpg")
        .fileUrl("https://test.com/image2.jpg")
        .build();

    uploadFile3 = UploadFile.builder()
        .fileName("image3.jpg")
        .fileUrl("https://test.com/image3.jpg")
        .build();

    uploadFileRepository.saveAll(List.of(uploadFile1, uploadFile2, uploadFile3));
  }
  @Test
  void debugDuplicateAdminThumbnails() {
    // 현재 DB에 있는 모든 Thumbnail 확인
    List<Thumbnail> allThumbnails = thumbnailRepository.findAll();
    System.out.println("=== 모든 Thumbnail ===");
    allThumbnails.forEach(t ->
        System.out.printf("ID: %d, Type: %s, AgentId: %s%n",
            t.getId(), t.getThumbnailType(), t.getAgentId())
    );

    // ADMIN 타입만 필터링
    List<Thumbnail> adminThumbnails = allThumbnails.stream()
        .filter(t -> t.getThumbnailType() == ThumbnailType.ADMIN)
        .toList();
    System.out.println("=== ADMIN Thumbnail 개수: " + adminThumbnails.size() + " ===");
  }

  @Test
  @DisplayName("관리자 썸네일이 없으면 새로 생성되어야 한다")
  void ensureAdminThumbnailExists_WhenNotExists_ShouldCreateNew() {
    // When
    Thumbnail result = thumbnailService.ensureAdminThumbnailExist();

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getThumbnailType()).isEqualTo(ThumbnailType.ADMIN);
    assertThat(result.getAgentId()).isNull();

    // DB 확인
    List<Thumbnail> savedThumbnails = thumbnailRepository.findAll();
    assertThat(savedThumbnails).hasSize(1);
    assertThat(savedThumbnails.get(0).getThumbnailType()).isEqualTo(ThumbnailType.ADMIN);

  }

  @Test
  @DisplayName("관리자 썸네일이 이미 있으면 기존 것을 반환해야 한다")
  void ensureAdminThumbnailExists_WhenExists_ShouldReturnExisting() {
    // Given
    Thumbnail existingThumbnail = Thumbnail.builder()
        .agentId(null)
        .thumbnailType(ThumbnailType.ADMIN)
        .build();
    thumbnailRepository.save(existingThumbnail);

    // When
    Thumbnail result1 = thumbnailService.ensureAdminThumbnailExist();
    Thumbnail result2 = thumbnailService.ensureAdminThumbnailExist();

    // Then
    assertThat(result1.getId()).isEqualTo(existingThumbnail.getId());
    assertThat(result2.getId()).isEqualTo(existingThumbnail.getId());

    // DB에는 여전히 하나만 있어야 함
    List<Thumbnail> thumbnails = thumbnailRepository.findAll();
    assertThat(thumbnails).hasSize(1);
  }

  @Test
  @DisplayName("공인중개사 썸네일 생성 테스트")
  void ensureAgentThumbnailExists_Test() {
    // Given
    Long agentId = 123L;

    // When
    Thumbnail result = thumbnailService.ensureAgentThumbnailExist(agentId);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getThumbnailType()).isEqualTo(ThumbnailType.AGENT);
    assertThat(result.getAgentId()).isEqualTo(agentId);
  }

  @Test
  @DisplayName("관리자 썸네일 이미지 업데이트 - 변경감지 테스트")
  @Transactional
  void updateAdminThumbnailImages_DirtyChecking_Test() {
    // Given
    ThumbnailReq req = new ThumbnailReq();
    req.setThumbnails(List.of(uploadFile1, uploadFile2, uploadFile3));

    // When
    thumbnailService.updateImages(req, ThumbnailType.ADMIN, null);

    // Then - 변경감지로 자동 저장되는지 확인
    List<Thumbnail> thumbnails = thumbnailRepository.findAll();
    assertThat(thumbnails).hasSize(1);

    Thumbnail savedThumbnail = thumbnails.get(0);
    assertThat(savedThumbnail.getThumbnailType()).isEqualTo(ThumbnailType.ADMIN);
    assertThat(savedThumbnail.getAgentId()).isNull();


    // 순서 확인
    List<UploadFile> savedImages = savedThumbnail.getThumbnails();
    assertThat(savedImages.get(0).getId()).isEqualTo(uploadFile1.getId());
    assertThat(savedImages.get(1).getId()).isEqualTo(uploadFile2.getId());
    assertThat(savedImages.get(2).getId()).isEqualTo(uploadFile3.getId());

    // 첫 번째가 대표이미지인지 확인
    assertThat(savedThumbnail.getMainThumbnail().getId()).isEqualTo(uploadFile1.getId());
  }

  @Test
  @DisplayName("공인중개사 썸네일 이미지 업데이트 테스트")
  void updateAgentThumbnailImages_Test() {
    // Given
    Long agentId = 456L;
    ThumbnailReq req = new ThumbnailReq();
    req.setThumbnails(List.of(uploadFile2, uploadFile1)); // 순서 바꿔서 테스트

    // When
    thumbnailService.updateImages(req, ThumbnailType.AGENT, agentId);

    // Then
    Thumbnail savedThumbnail = thumbnailRepository.findByThumbnailTypeAndAgentId(ThumbnailType.AGENT, agentId)
        .orElseThrow();

    assertThat(savedThumbnail.getThumbnails()).hasSize(2);

    // 순서 확인 (uploadFile2가 첫 번째여야 함)
    List<UploadFile> savedImages = savedThumbnail.getThumbnails();
    assertThat(savedImages.get(0).getId()).isEqualTo(uploadFile2.getId());
    assertThat(savedImages.get(1).getId()).isEqualTo(uploadFile1.getId());

    // 대표이미지 확인 (첫 번째 = uploadFile2)
    assertThat(savedThumbnail.getMainThumbnail().getId()).isEqualTo(uploadFile2.getId());
  }

  @Test
  @DisplayName("썸네일 이미지 교체 테스트 - clear 후 addAll 확인")
  void updateThumbnailImages_Replace_Test() {
    // Given - 초기 데이터 설정
    ThumbnailReq initialReq = new ThumbnailReq();
    initialReq.setThumbnails(List.of(uploadFile1, uploadFile2));
    thumbnailService.updateImages(initialReq, ThumbnailType.ADMIN, null);

    // 초기 상태 확인
    Thumbnail thumbnail = thumbnailRepository.findByThumbnailTypeAndAgentIdIsNull(ThumbnailType.ADMIN)
        .orElseThrow();
    assertThat(thumbnail.getThumbnails()).hasSize(2);

    // When - 새로운 이미지로 교체
    ThumbnailReq updateReq = new ThumbnailReq();
    updateReq.setThumbnails(List.of(uploadFile3)); // 완전히 다른 이미지로 교체
    thumbnailService.updateImages(updateReq, ThumbnailType.ADMIN, null);

    // Then - 기존 이미지는 제거되고 새 이미지만 있어야 함
    thumbnail = thumbnailRepository.findByThumbnailTypeAndAgentIdIsNull(ThumbnailType.ADMIN)
        .orElseThrow();

    assertThat(thumbnail.getThumbnails()).hasSize(1);
    assertThat(thumbnail.getThumbnails().get(0).getId()).isEqualTo(uploadFile3.getId());
    assertThat(thumbnail.getMainThumbnail().getId()).isEqualTo(uploadFile3.getId());
  }

  @Test
  @DisplayName("빈 리스트로 업데이트 시 모든 이미지가 제거되어야 한다")
  void updateThumbnailImages_EmptyList_Test() {
    // Given - 초기 데이터 설정
    ThumbnailReq initialReq = new ThumbnailReq();
    initialReq.setThumbnails(List.of(uploadFile1, uploadFile2));
    thumbnailService.updateImages(initialReq, ThumbnailType.ADMIN, null);

    // When - 빈 리스트로 업데이트
    ThumbnailReq emptyReq = new ThumbnailReq();
    emptyReq.setThumbnails(List.of()); // 빈 리스트
    thumbnailService.updateImages(emptyReq, ThumbnailType.ADMIN, null);

    // Then - 모든 이미지가 제거되어야 함
    Thumbnail thumbnail = thumbnailRepository.findByThumbnailTypeAndAgentIdIsNull(ThumbnailType.ADMIN)
        .orElseThrow();

    assertThat(thumbnail.getThumbnails()).isEmpty();
    assertThat(thumbnail.getMainThumbnail()).isNull();
  }

  @Test
  @DisplayName("null 이미지 리스트로 업데이트 시 변경되지 않아야 한다")
  void updateThumbnailImages_NullList_Test() {
    // Given - 초기 데이터 설정
    ThumbnailReq initialReq = new ThumbnailReq();
    initialReq.setThumbnails(List.of(uploadFile1, uploadFile2));
    thumbnailService.updateImages(initialReq, ThumbnailType.ADMIN, null);

    // When - null로 업데이트 시도
    ThumbnailReq nullReq = new ThumbnailReq();
    nullReq.setThumbnails(null); // null
    thumbnailService.updateImages(nullReq, ThumbnailType.ADMIN, null);

    // Then - 기존 데이터 유지되어야 함
    Thumbnail thumbnail = thumbnailRepository.findByThumbnailTypeAndAgentIdIsNull(ThumbnailType.ADMIN)
        .orElseThrow();

    assertThat(thumbnail.getThumbnails()).hasSize(2);
    assertThat(thumbnail.getMainThumbnail().getId()).isEqualTo(uploadFile1.getId());
  }

  @Test
  @DisplayName("여러 공인중개사가 각각 다른 썸네일을 가질 수 있어야 한다")
  void multipleAgents_SeparateThumbnails_Test() {
    // Given
    Long agent1Id = 100L;
    Long agent2Id = 200L;

    ThumbnailReq req1 = new ThumbnailReq();
    req1.setThumbnails(List.of(uploadFile1));

    ThumbnailReq req2 = new ThumbnailReq();
    req2.setThumbnails(List.of(uploadFile2, uploadFile3));

    // When
    thumbnailService.updateImages(req1, ThumbnailType.AGENT, agent1Id);
    thumbnailService.updateImages(req2, ThumbnailType.AGENT, agent2Id);

    // Then
    Thumbnail agent1Thumbnail = thumbnailRepository.findByThumbnailTypeAndAgentId(ThumbnailType.AGENT, agent1Id)
        .orElseThrow();
    Thumbnail agent2Thumbnail = thumbnailRepository.findByThumbnailTypeAndAgentId(ThumbnailType.AGENT, agent2Id)
        .orElseThrow();

    // Agent1: 이미지 1개
    assertThat(agent1Thumbnail.getThumbnails()).hasSize(1);
    assertThat(agent1Thumbnail.getMainThumbnail().getId()).isEqualTo(uploadFile1.getId());

    // Agent2: 이미지 2개
    assertThat(agent2Thumbnail.getThumbnails()).hasSize(2);
    assertThat(agent2Thumbnail.getMainThumbnail().getId()).isEqualTo(uploadFile2.getId());

    // 서로 독립적이어야 함
    assertThat(agent1Thumbnail.getId()).isNotEqualTo(agent2Thumbnail.getId());
  }
}