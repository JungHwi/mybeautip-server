package com.jocoos.mybeautip.client.aws.s3;

import com.jocoos.mybeautip.global.code.FileOperationType;
import com.jocoos.mybeautip.global.code.UrlDirectory;
import com.jocoos.mybeautip.global.dto.FileDto;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.exception.S3UrlUploadException;
import com.jocoos.mybeautip.support.RandomUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static com.jocoos.mybeautip.global.util.FileUtil.getFilename;
import static com.jocoos.mybeautip.global.util.ImageUrlConvertUtil.getUri;

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

    public String upload(String url, String directory, String defaultFilename) {
        String filename = RandomUtils.generateFilename();
        try {
            String path = service.upload(url, directory + filename);
            return cloudFront + path;
        } catch (S3UrlUploadException e) {
            return cloudFront + defaultFilename;
        }
    }

    public String copy(FileDto fileDto, String destination) {
        if (fileDto.getOperation() == FileOperationType.DELETE) {
            return "";
        }

        String filename = getFilename(fileDto.getUrl());

        String path = service.copy(
                UrlDirectory.TEMP.getDirectory() + filename,
                destination + filename);

        return cloudFront + path;
    }

    public List<String> copy(List<FileDto> fileDtoList, String destination) {
        if (CollectionUtils.isEmpty(fileDtoList)) {
            return null;
        }

        List<String> result = new ArrayList<>();
        for (FileDto fileDto : fileDtoList) {
            result.add(copy(fileDto, destination));
        }

        return result;
    }

    public List<String> editFiles(List<FileDto> fileDtoList, String destination) {
        if (CollectionUtils.isEmpty(fileDtoList)) {
            return null;
        }

        List<String> result = new ArrayList<>();
        for (FileDto fileDto : fileDtoList) {
            switch (fileDto.getOperation()) {
                case UPLOAD:
                    result.add(copy(fileDto, destination));
                    break;
                case DELETE:
                    delete(fileDto);
                    break;
                default:
                    throw new BadRequestException("not_support_file_operation");
            }
        }
        return result;
    }

    public void delete(FileDto fileDto) {
        String filename = getUri(fileDto.getUrl());
        service.delete(filename);
    }
}