package com.jocoos.mybeautip.client.aws.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.jocoos.mybeautip.global.config.aws.AwsCredentialService;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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

    public String upload(MultipartFile file, String contentType, String key) {
        AmazonS3 s3Client = credentialService.getS3Client();
        ObjectMetadata metadata = new ObjectMetadata();
        PutObjectResult result;
        try {
            metadata.setContentLength(file.getBytes().length);
            metadata.setContentType(contentType);

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file.getInputStream(), metadata);
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
        } catch (MalformedURLException e) {
            throw new BadRequestException(ErrorCode.BAD_REQUEST, "wrong url : " + urlString);
        } catch (IOException e) {
            //TODO Exception 정의
            throw new RuntimeException("File Upload ERROR", e);
        } catch (AmazonServiceException e) {
            throw new RuntimeException("AWS S3 ERROR!! - UPLOAD" , e);
        } catch (SdkClientException e) {
            throw new RuntimeException("sdk client exception - upload" , e);
        }
    }

    public String copy(String sourceKey, String destinationKey) {
        CopyObjectRequest copyObjectRequest = new CopyObjectRequest(bucketName, sourceKey, bucketName, destinationKey);

        return copy(copyObjectRequest);
    }

    public void delete(String targetKey) {
        try {
            AmazonS3 s3Client = credentialService.getS3Client();
            s3Client.deleteObject(new DeleteObjectRequest(bucketName, targetKey));
        } catch (AmazonS3Exception ex) {
            log.warn("AWS S3 Delete Error Message : {}", ex.getErrorMessage());
        }
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
            throw new BadRequestException(ErrorCode.S3_ERROR, ex.getErrorMessage());
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
        return new PutObjectRequest(bucketName, key, inputStream, metadata);
    }

    private void closeConnection(HttpURLConnection conn, InputStream inputStream) throws IOException {
        inputStream.close();
        conn.disconnect();
    }
}
