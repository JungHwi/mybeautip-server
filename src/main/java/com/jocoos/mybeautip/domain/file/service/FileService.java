package com.jocoos.mybeautip.domain.file.service;

import com.jocoos.mybeautip.client.aws.s3.AwsS3Handler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static com.jocoos.mybeautip.global.code.UrlDirectory.TEMP_IMAGE;
import static com.jocoos.mybeautip.global.code.UrlDirectory.TEMP_VIDEO;

@RequiredArgsConstructor
@Service
public class FileService {

    private final AwsS3Handler awsS3Handler;

    public List<String> upload(List<MultipartFile> files) {
        List<String> fileUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            fileUrls.add(upload(file));
        }
        return fileUrls;
    }

    private String upload(MultipartFile file) {
        if (isImage(file)) {
            return awsS3Handler.upload(file, TEMP_IMAGE.getDirectory());
        }
        return awsS3Handler.upload(file, TEMP_VIDEO.getDirectory());
    }

    private boolean isImage(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    public List<String> uploadV1(List<MultipartFile> files) {
        return awsS3Handler.upload(files, TEMP_IMAGE.getDirectory());
    }
}
