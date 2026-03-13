package com.backend.lab.application.port.in.property.file;

import org.springframework.web.multipart.MultipartFile;

public interface UploadExcelUseCase {
    void uploadExcelFile(MultipartFile file);
}
