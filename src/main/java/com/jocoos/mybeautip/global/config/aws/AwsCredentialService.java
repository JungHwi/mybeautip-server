package com.jocoos.mybeautip.global.config.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AwsCredentialService {
    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${cloud.aws.credentials.accessKey}")
    private String awsAccessKeyID;

    @Value("${cloud.aws.credentials.secretKey}")
    private String awsSecretAccessKey;

    public AmazonS3 getS3Client() {
        AWSCredentials awsCredentials = new BasicAWSCredentials(awsAccessKeyID, awsSecretAccessKey);
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(region)
                .build();
    }
}
