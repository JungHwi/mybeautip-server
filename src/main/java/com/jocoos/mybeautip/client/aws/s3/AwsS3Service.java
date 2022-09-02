package com.jocoos.mybeautip.client.aws.s3;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.jocoos.mybeautip.global.config.aws.AwsCredentialService;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.exception.S3UrlUploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
@Component
@RequiredArgsConstructor
public class AwsS3Service {

    private final AwsCredentialService credentialService;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${mybeautip.aws.s3.connection-timeout-ms}")
    private int connectionTimeout;

    @Value("${mybeautip.aws.s3.read-timeout-ms}")
    private int readTimeout;

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

    public String upload(String urlString, String key) {
        try {
            AmazonS3 s3Client = credentialService.getS3Client();

            URL url = new URL(urlString);
            HttpURLConnection conn = getHeadHttpURLConnection(url);

            ObjectMetadata metadata = getMetadata(conn.getContentLengthLong(), conn.getContentType());

            InputStream inputStream = url.openStream();
            PutObjectRequest request = getPutObjectRequest(key, inputStream, metadata);
            s3Client.putObject(request);

            closeConnection(conn, inputStream);
            return key;
        } catch (IOException | SdkClientException e) {
            log.info("{} Cause At Avatar Url Upload, Request URL : {}", e.getClass().getName(), urlString);
            throw new S3UrlUploadException(e);
        }
    }

    public String copy(String sourceKey, String destinationKey) {
        CopyObjectRequest copyObjectRequest = new CopyObjectRequest(bucketName, sourceKey, bucketName, destinationKey);
        copyObjectRequest.withCannedAccessControlList(CannedAccessControlList.PublicRead);

        return copy(copyObjectRequest);
    }

    public void delete(String targetKey) {
        AmazonS3 s3Client = credentialService.getS3Client();
        s3Client.deleteObject(new DeleteObjectRequest(bucketName, targetKey));
    }

    private String copy(CopyObjectRequest copyObjectRequest) {
        AmazonS3 s3Client = credentialService.getS3Client();
        try {
            CopyObjectResult result = s3Client.copyObject(copyObjectRequest);

            if (result == null) {
                throw new RuntimeException("AWS S3 ERROR!! - COPY");
            }

            delete(copyObjectRequest.getSourceKey());
        } catch (AmazonS3Exception ex) {
            throw new BadRequestException("s3_error", ex.getErrorMessage());
        }

        return copyObjectRequest.getDestinationKey();
    }

    private HttpURLConnection getHeadHttpURLConnection(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("HEAD");
        conn.setConnectTimeout(this.connectionTimeout);
        conn.setReadTimeout(this.readTimeout);
        return conn;
    }

    private ObjectMetadata getMetadata(Long contentLength, String contentType) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(contentLength);
        metadata.setContentType(contentType);
        return metadata;
    }

    private PutObjectRequest getPutObjectRequest(String key, InputStream inputStream, ObjectMetadata metadata) {
        PutObjectRequest request = new PutObjectRequest(bucketName, key, inputStream, metadata);
        request.setCannedAcl(CannedAccessControlList.PublicRead);
        return request;
    }

    private void closeConnection(HttpURLConnection conn, InputStream inputStream) throws IOException {
        inputStream.close();
        conn.disconnect();
    }
}