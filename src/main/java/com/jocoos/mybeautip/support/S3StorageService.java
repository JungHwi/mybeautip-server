package com.jocoos.mybeautip.support;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.amazonaws.services.securitytoken.model.GetSessionTokenRequest;
import com.amazonaws.services.securitytoken.model.GetSessionTokenResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
public class S3StorageService implements StorageService {

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.credentials.accessKey}")
    private String awsAccessKeyID;

    @Value("${cloud.aws.credentials.secretKey}")
    private String awsSecretAccessKey;

    @Value("${cloud.aws.sts.token-validity-duration-seconds}")
    private String tokenValidityDurations;

    public Storage getStorageInfo() {
        Storage storage = new Storage(region, bucketName);
        storage.setCredentials(getAWSSTSCredentials());

        return storage;
    }

    public Credentials getAWSSTSCredentials() {
        AWSCredentials awsCredentials = new BasicAWSCredentials(awsAccessKeyID, awsSecretAccessKey);
        int durationSeconds = Integer.parseInt(tokenValidityDurations);

        AWSSecurityTokenService stsClient = AWSSecurityTokenServiceClientBuilder.standard()
                .withRegion(Regions.AP_NORTHEAST_2)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();

        GetSessionTokenRequest request = new GetSessionTokenRequest().withDurationSeconds(durationSeconds);
        GetSessionTokenResult result = stsClient.getSessionToken(request);

        return result.getCredentials();
    }

    private AmazonS3 getS3Client() {
        AWSCredentials awsCredentials = new BasicAWSCredentials(awsAccessKeyID, awsSecretAccessKey);
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(region)
                .build();
    }

    public String upload(MultipartFile file, String key) throws IOException {
        upload(file, key, true);
        return key;
    }

    public String upload(MultipartFile file, String key, boolean publicRead) throws IOException {
        AmazonS3 s3Client = getS3Client();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getBytes().length);

        PutObjectRequest putObjectRequest = new PutObjectRequest(
                bucketName, key, file.getInputStream(), metadata);

        s3Client.putObject(putObjectRequest);
        return s3Client.getUrl(bucketName, key).toString();
    }

    public String upload(File file, String key, boolean publicRead) throws IOException {
        AmazonS3 s3Client = getS3Client();
        PutObjectRequest putObjectRequest = new PutObjectRequest(
                bucketName, key, file);

        s3Client.putObject(putObjectRequest);
        return s3Client.getUrl(bucketName, key).toString();
    }

    public String upload(InputStream inputStream, String key, Long contentLength, boolean publicRead) throws IOException {
        AmazonS3 s3Client = getS3Client();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(contentLength);
        PutObjectRequest putObjectRequest = new PutObjectRequest(
                bucketName, key, inputStream, objectMetadata);

        s3Client.putObject(putObjectRequest);
        return s3Client.getUrl(bucketName, key).toString();
    }

    public AwsInfo createAwsUploadInfo(String key) {
        AwsInfo info = new AwsInfo(region, bucketName, key);
        log.debug("{}", info);
        return info;
    }

    private S3ObjectInputStream getObject(String key) {
        AmazonS3 s3Client = getS3Client();
        S3Object object = s3Client.getObject(bucketName, key);
        return object.getObjectContent();
    }

    public Resource getResource(String key) {
        try {
            S3ObjectInputStream inputStream = getObject(key);
            Resource resource = new InputStreamResource(inputStream);
            return resource;
        } catch (AmazonS3Exception e) {
            log.error("{}", e);
            return null;
        }
    }

    @Override
    public void delete(String key) {
        try {
            AmazonS3 s3Client = getS3Client();
            s3Client.deleteObject(new DeleteObjectRequest(bucketName, key));
            log.info("Deleted {}", key);
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            log.error("{}", e);
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            log.error("{}", e);
        }
    }

    public void delete(List<String> keys) {
        for (String key : keys) {
            delete(key);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AwsInfo {
        private String region;
        private String bucket;
        private String key;
    }

    @Data
    @NoArgsConstructor
    public static class Storage {
        private String region;
        private String bucket;
        private String accessKeyId;
        private String secretAccessKey;
        private String sessionToken;
        private Long expiration;

        public Storage(String region, String bucket) {
            this.region = region;
            this.bucket = bucket;
        }

        public void setCredentials(Credentials credentials) {
            this.accessKeyId = credentials.getAccessKeyId();
            this.secretAccessKey = credentials.getSecretAccessKey();
            this.sessionToken = credentials.getSessionToken();
            this.expiration = credentials.getExpiration().getTime();
        }
    }
}
