package com.backend.lab.application.port.in.property.file;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface DownloadCsvUseCase {
    ByteArrayInputStream createFastCsvFile(Long adminId, List<Long> ids);
}
