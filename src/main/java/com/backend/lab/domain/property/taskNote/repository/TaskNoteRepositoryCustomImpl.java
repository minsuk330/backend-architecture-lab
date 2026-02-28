package com.backend.lab.domain.property.taskNote.repository;

import com.backend.lab.api.admin.tasknote.dto.TaskNoteOptions;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.admin.core.entity.QAdmin;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.property.core.entity.QProperty;
import com.backend.lab.domain.property.core.entity.information.QAddressInformation;
import com.backend.lab.domain.property.taskNote.entity.QTaskNote;
import com.backend.lab.domain.property.taskNote.entity.TaskNote;
import com.backend.lab.domain.property.taskNote.entity.dto.req.SearchTaskNoteOptions;
import com.backend.lab.domain.property.taskNote.entity.dto.resp.TaskNoteSearchResp;
import com.backend.lab.domain.uploadFile.entity.UploadFile;
import com.backend.lab.domain.uploadFile.entity.dto.resp.UploadFileResp;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@RequiredArgsConstructor
public class TaskNoteRepositoryCustomImpl implements TaskNoteRepositoryCustom {

  private final JPAQueryFactory queryFactory;
  private final QTaskNote qtaskNote = QTaskNote.taskNote;
  private final QProperty qproperty = QProperty.property;
  private final QAdmin qadmin = QAdmin.admin;
  private final QAddressInformation qAddress = QAddressInformation.addressInformation;



  @Override
  public Page<TaskNoteSearchResp> search(SearchTaskNoteOptions options) {
    BooleanBuilder booleanBuilder = buildSearchPredicates(options);

    // TaskNote 엔티티 조회
    List<TaskNote> taskNotes = queryFactory
        .selectFrom(qtaskNote)
        .leftJoin(qtaskNote.property, qproperty).fetchJoin()
        .leftJoin(qtaskNote.createdBy, qadmin).fetchJoin()
        .leftJoin(qproperty.address, qAddress).fetchJoin()
        .where(booleanBuilder
            .and(qtaskNote.createdBy.isNotNull())
            .and(qtaskNote.deletedAt.isNull())
            .and(qtaskNote.property.delete_persistent_at.isNull()))
        .offset(options.pageable().getOffset())
        .limit(options.pageable().getPageSize())
        .orderBy(qtaskNote.createdAt.desc())
        .fetch();

    List<TaskNoteSearchResp> rows = taskNotes.stream()
        .map(this::convertToResp)
        .collect(Collectors.toList());

    Long total = queryFactory
        .select(qtaskNote.count())
        .from(qtaskNote)
        .leftJoin(qtaskNote.property, qproperty)
        .leftJoin(qtaskNote.createdBy, qadmin)
        .where(booleanBuilder
            .and(qtaskNote.createdBy.isNotNull())
            .and(qtaskNote.deletedAt.isNull())
            .and(qtaskNote.property.delete_persistent_at.isNull()))
        .fetchOne();

    return new PageImpl<>(rows, options.pageable(), total != null ? total : 0);
  }

  private TaskNoteSearchResp convertToResp(TaskNote taskNote) {
    Property property = taskNote.getProperty();
    Admin createdBy = taskNote.getCreatedBy();
    boolean deletedAt = taskNote.getProperty().getDeletedAt() != null;
    
    return TaskNoteSearchResp.builder()
        .id(taskNote.getId())
        .createdAt(taskNote.getCreatedAt())
        .updatedAt(taskNote.getUpdatedAt())

        .propertyDeleted(deletedAt)
        .createdId(createdBy != null ? createdBy.getId() : null)
        .propertyId(property.getId())
        .buildingName(property.getBuildingName())
        .address(property.getAddress().getProperties().getJibunAddress())
        .content(taskNote.getContent())
        .fileUrl(convertToUploadFileResp(taskNote.getImage()))
        .createdBy(createdBy != null ? createdBy.getName() : "삭제된 사용자")
        .taskType(taskNote.getType())
        .before(taskNote.getBeforeValue())
        .after(taskNote.getAfterValue())
        .build();
  }

  private UploadFileResp convertToUploadFileResp(UploadFile uploadFile) {
    if (uploadFile == null) {
      return null;
    }

    return UploadFileResp.builder()
        .id(uploadFile.getId())
        .createdAt(uploadFile.getCreatedAt())

        .fileName(uploadFile.getFileName())
        .fileUrl(uploadFile.getFileUrl())
        .build();

  }

  @Override
  public Page<TaskNote> findAllByPropertyId(TaskNoteOptions options) {

    List<TaskNote> rows = queryFactory.selectFrom(qtaskNote)
        .leftJoin(qtaskNote.createdBy, qadmin).fetchJoin()
        .leftJoin(qtaskNote.property, qproperty)
        .where(qtaskNote.property.id.eq(options.getPropertyId())
            .and(qtaskNote.createdBy.isNotNull())
            .and(qtaskNote.deletedAt.isNull()))
        .offset(options.pageable().getOffset())
        .limit(options.pageable().getPageSize())
        .orderBy(qtaskNote.createdAt.desc())
        .fetch();


    Long total = queryFactory
        .select(qtaskNote.count())
        .from(qtaskNote)
        .leftJoin(qtaskNote.property, qproperty)
        .leftJoin(qtaskNote.createdBy, qadmin)
        .where(qtaskNote.property.id.eq(options.getPropertyId())
            .and(qtaskNote.createdBy.isNotNull())
            .and(qtaskNote.deletedAt.isNull()))
        .fetchOne();

    return new PageImpl<>(rows, options.pageable(), total != null ? total : 0);
  }

  public BooleanBuilder buildSearchPredicates(SearchTaskNoteOptions options) {
    BooleanBuilder builder = new BooleanBuilder();

    if(options.getQuery()!=null && !options.getQuery().isBlank()) {
      String query = options.getQuery().trim();

      String[] keywords = query.split("\\s+");

      BooleanExpression addressQuery = null;

      for (String keyword : keywords) {
        BooleanExpression keywordMatch =
            qproperty.address.properties.jibunAddress.containsIgnoreCase(keyword)
                .or(qproperty.address.properties.roadAddress.containsIgnoreCase(keyword));

        addressQuery = (addressQuery == null) ? keywordMatch : addressQuery.and(keywordMatch);
      }

      BooleanExpression queryExp =
          qproperty.buildingName.containsIgnoreCase(options.getQuery())
          .or(addressQuery)
          .or(qproperty.id.stringValue().contains(options.getQuery()));
      builder.and(queryExp);

    }
    if(options.getTaskType() != null) {
      builder.and(qtaskNote.type.eq(options.getTaskType()));
    }
    if(options.getAdminId() != null && options.getAdminId().describeConstable().isPresent()) {
      builder.and(qtaskNote.createdBy.id.eq(options.getAdminId()));
    }

    return builder;
  }
}
