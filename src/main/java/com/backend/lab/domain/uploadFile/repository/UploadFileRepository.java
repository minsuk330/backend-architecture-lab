package com.backend.lab.domain.uploadFile.repository;

import com.backend.lab.domain.uploadFile.entity.UploadFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadFileRepository extends JpaRepository<UploadFile, Long> {

}
