package com.jocoos.mybeautip.client.aws.s3;

import com.jocoos.mybeautip.global.code.FileOperationType;
import com.jocoos.mybeautip.global.code.UrlDirectory;
import com.jocoos.mybeautip.global.dto.FileDto;
import com.jocoos.mybeautip.support.RandomUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static com.jocoos.mybeautip.global.util.FileUtil.getFilename;

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
}
