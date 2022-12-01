package com.jocoos.mybeautip.global.config.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AwsCredentialService {
    @Value("${cloud.aws.region.static}")
    private String region;

    public AmazonS3 getS3Client() {
        return AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .build();
    }
}
