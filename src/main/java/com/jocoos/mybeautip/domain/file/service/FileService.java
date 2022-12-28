package com.jocoos.mybeautip.domain.file.service;

import com.jocoos.mybeautip.client.aws.s3.AwsS3Handler;
import com.jocoos.mybeautip.global.code.UrlDirectory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@Service
public class FileService {

    private final AwsS3Handler awsS3Handler;

    public List<String> upload(List<MultipartFile> files) {
        List<String> imageUrls = getImageFileStream(files);
        List<String> videoUrls = getVideoFileStream(files);
        imageUrls.addAll(videoUrls);
        return imageUrls;
    }

    private List<String> getImageFileStream(List<MultipartFile> files) {
        List<MultipartFile> images = files.stream()
                .filter(this::isImage)
                .toList();
        return awsS3Handler.upload(images, UrlDirectory.TEMP_IMAGE.getDirectory());
    }

    private List<String> getVideoFileStream(List<MultipartFile> files) {
        List<MultipartFile> videos = files.stream()
                .filter(this::isVideo)
                .toList();
        return awsS3Handler.upload(videos, UrlDirectory.TEMP_VIDEO.getDirectory());
    }

    private boolean isImage(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    private boolean isVideo(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("video/");
    }
}
