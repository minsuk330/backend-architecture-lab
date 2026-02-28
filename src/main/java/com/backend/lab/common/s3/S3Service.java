package com.backend.lab.common.s3;

import com.backend.lab.api.common.uploadFile.dto.req.FileUploadReq;
import com.backend.lab.common.s3.dto.PreSignedUrlResp;
import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class S3Service {

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;
  @Value("${cloud.aws.s3.region}")
  private String region;

  private final S3Presigner s3Presigner;
  private final S3Client s3Client;

  public PreSignedUrlResp getPreSignedUrl(FileUploadReq req) {

    String key = UUID.randomUUID().toString();

    try {
      String encodedFileName = URLEncoder.encode(req.getFilename(), StandardCharsets.UTF_8)
          .replace("+", "%20");
      String contentDisposition = "attachment; filename=\"" + encodedFileName + "\"";

      PutObjectRequest putObjectRequest = PutObjectRequest.builder()
          .bucket(bucket)
          .key(key)
          .contentDisposition(contentDisposition)
          .build();

      PutObjectPresignRequest putObjectPresignRequest = PutObjectPresignRequest.builder()
          .signatureDuration(Duration.ofMinutes(5))
          .putObjectRequest(putObjectRequest)
          .build();

      PresignedPutObjectRequest presignedPutObjectRequest = s3Presigner.presignPutObject(
          putObjectPresignRequest);

      String uploadUrl = presignedPutObjectRequest.url().toString();
      String accessUrl = this.getAccessUrlFromUploadUrl(uploadUrl);

      return PreSignedUrlResp.builder()
          .fileName(req.getFilename())
          .uploadUrl(uploadUrl)
          .accessUrl(accessUrl)
          .contentDisposition(contentDisposition)
          .build();
    } catch (Exception e) {
      throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
    }
  }

  public String getAccessUrlFromUploadUrl(String uploadUrl) {
    if (uploadUrl == null || (!uploadUrl.contains("amazonaws.com") && !uploadUrl.contains(bucket))) {
      return null;
    }
    String key = uploadUrl.substring(uploadUrl.lastIndexOf("/") + 1);
    key = key.split("\\?")[0];
    return "https://" + "s3." + region + ".amazonaws.com/" + bucket + "/" + key;
  }

  public void deleteByAccessUrl(String accessUrl) {

    if (accessUrl == null || !(accessUrl.contains("amazonaws.com") && accessUrl.contains(bucket))) {
      return;
    }

    String key = accessUrl.substring(accessUrl.lastIndexOf("/") + 1);

    DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build();

    s3Client.deleteObject(deleteObjectRequest);
  }

  public void deleteByAccessUrl(List<String> imageUrl) {
    for (String url : imageUrl) {
      this.deleteByAccessUrl(url);
    }
  }


}
