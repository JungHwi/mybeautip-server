package com.jocoos.mybeautip.client.aws.s3;

import com.jocoos.mybeautip.support.RandomUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AwsS3Handler {

    private final AwsS3Service service;

    @Value("${mybeautip.aws.cf.domain}")
    private String cloudFront;

    public List<String> upload(List<MultipartFile> files, String directory) {
        List<String> result = new ArrayList<>();

        for (MultipartFile file : files) {
            result.add(upload(file, directory));
        }

        return result;
    }

    public String upload(MultipartFile file, String directory) {
        String filename = RandomUtils.generateFilename();

        String path = service.upload(file, directory + filename);

        return cloudFront + path;
    }
}
