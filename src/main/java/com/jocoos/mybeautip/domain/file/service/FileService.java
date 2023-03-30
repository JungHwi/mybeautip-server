package com.jocoos.mybeautip.domain.file.service;

import com.jocoos.mybeautip.client.aws.s3.AwsS3Handler;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static com.jocoos.mybeautip.global.code.UrlDirectory.TEMP_IMAGE;
import static com.jocoos.mybeautip.global.code.UrlDirectory.TEMP_VIDEO;

@Log4j2
@RequiredArgsConstructor
@Service
public class FileService {

    private final AwsS3Handler awsS3Handler;
    private final FileContentTypeService contentTypeService;

    public List<String> upload(List<MultipartFile> files) {
        List<String> fileUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            fileUrls.add(upload(file));
        }
        return fileUrls;
    }

    public String uploadWithDefault(MultipartFile file, String defaultUrl) {
        try {
            return upload(file);
        } catch (Exception e) {
            log.warn("Fail to upload file. Default Url will return.", e);
            return defaultUrl;
        }
    }

    public String upload(MultipartFile file) {
        String contentType = contentTypeService.getContentType(file);
        return upload(file, contentType);
    }

    private String upload(MultipartFile file, String contentType) {
        if (isImage(contentType)) {
            return awsS3Handler.upload(file, contentType, TEMP_IMAGE.getDirectory());
        }
        return awsS3Handler.upload(file, contentType, TEMP_VIDEO.getDirectory());
    }

    private boolean isImage(String contentType) {
        return contentType != null && contentType.startsWith("image/");
    }
}
