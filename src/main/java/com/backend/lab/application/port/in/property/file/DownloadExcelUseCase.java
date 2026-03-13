package com.backend.lab.application.port.in.property.file;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface DownloadExcelUseCase {
    ByteArrayInputStream createExcelFile(Long adminId, List<Long> ids);
}
