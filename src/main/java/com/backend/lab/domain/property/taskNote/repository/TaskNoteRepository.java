package com.backend.lab.domain.property.taskNote.repository;

import com.backend.lab.domain.property.taskNote.entity.TaskNote;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskNoteRepository extends JpaRepository<TaskNote, Long> , TaskNoteRepositoryCustom {

  @Query("select t from TaskNote t where t.property.id = :propertyId and t.deletedAt is null order by t.createdAt desc ")
  List<TaskNote> findAllByPropertyId(@Param("propertyId") Long propertyId);

  @Query("select t from TaskNote t where t.createdBy.id = :adminId")
  List<TaskNote> findAllByAdminId(@Param("adminId") Long adminId);

  @Modifying
  @Query("DELETE FROM TaskNote t WHERE t.property.id = :propertyId")
  void deleteByPropertyId(@Param("propertyId") Long propertyId);

  @Query("select t from TaskNote t where t.property.delete_persistent_at is null order by t.createdAt desc limit :limit")
  List<TaskNote> findByOrderByCreatedAtDesc(@Param("limit")int limit);

  @Query("SELECT DISTINCT a.name " +
      "FROM TaskNote t " +
      "JOIN t.createdBy a " +
      "WHERE a IS NOT NULL " +
      "ORDER BY a.name ASC")
  List<String> findAllAdminNamesInTaskNotes();

  @Modifying
  @Query(value = "INSERT INTO task_note (property_id, type, created_at, updated_at, content, create_by_admin_id) " +
                 "VALUES (?1, ?2, ?3, ?3, ?4, ?5)", nativeQuery = true)
  void insertTaskNote(Long propertyId, String type, LocalDateTime createdAt, String content, Long adminId);

  @Modifying  
  @Query(value = "INSERT INTO task_note (property_id, type, created_at, updated_at, before_value, after_value, log_field_type, create_by_admin_id) " +
                 "VALUES (?1, ?2, ?3, ?3, ?4, ?5, ?6, ?7)", nativeQuery = true)
  void insertTaskNoteAuto(Long propertyId, String type, LocalDateTime createdAt, String beforeValue, String afterValue, Integer logFieldType, Long adminId);
}
