package com.jocoos.mybeautip.client.aws.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.jocoos.mybeautip.global.config.aws.AwsCredentialService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AwsS3Service {

    private final AwsCredentialService credentialService;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String upload(MultipartFile file, String key) {
        AmazonS3 s3Client = credentialService.getS3Client();
        ObjectMetadata metadata = new ObjectMetadata();
        PutObjectResult result;
        try {
            metadata.setContentLength(file.getBytes().length);
            metadata.setContentType(file.getContentType());

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file.getInputStream(), metadata);
            putObjectRequest.setCannedAcl(CannedAccessControlList.PublicRead);

            result = s3Client.putObject(putObjectRequest);
        } catch (IOException ex) {
            throw new RuntimeException("File Upload ERROR");
        }

        if (result == null) {
            throw new RuntimeException("AWS S3 ERROR!! - UPLOAD");
        }

        return key;
    }

    public String copy(String sourceKey, String destinationKey) {
        return copy(bucketName, sourceKey, bucketName, destinationKey);
    }

    private String copy(String sourceBucketName, String sourceKey, String destinationBucketName, String destinationKey) {
        AmazonS3 s3Client = credentialService.getS3Client();
        CopyObjectResult result = s3Client.copyObject(sourceBucketName, sourceKey, destinationBucketName, destinationKey);

        if (result == null) {
            throw new RuntimeException("AWS S3 ERROR!! - COPY");
        }

        s3Client.deleteObject(sourceBucketName, sourceKey);
        return destinationKey;
    }
}
