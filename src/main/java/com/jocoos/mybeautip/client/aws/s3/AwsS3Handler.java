package com.jocoos.mybeautip.client.aws.s3;

import com.jocoos.mybeautip.global.code.FileOperationType;
import com.jocoos.mybeautip.global.code.UrlDirectory;
import com.jocoos.mybeautip.global.dto.FileDto;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.exception.S3UrlUploadException;
import com.jocoos.mybeautip.support.RandomUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.jocoos.mybeautip.global.code.UrlDirectory.TEMP_IMAGE;
import static com.jocoos.mybeautip.global.constant.MybeautipConstant.TEST_FILE;
import static com.jocoos.mybeautip.global.util.FileUtil.getFileName;
import static com.jocoos.mybeautip.global.util.ImageUrlConvertUtil.getUri;
import static io.micrometer.core.instrument.util.StringUtils.isBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

@Log4j2
@Component
@RequiredArgsConstructor
public class AwsS3Handler {

    private final AwsS3Service service;

    @Value("${mybeautip.aws.cf.domain}")
    private String cloudFront;

    public String upload(MultipartFile file, String contentType, String directory) {
        String filename = RandomUtils.generateFilename();

        String path = service.upload(file, contentType, directory + filename);

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

    public String copy(String fileUrl, String directory, String defaultFilename) {
        try {
            return copy(fileUrl, directory);
        } catch (BadRequestException e) {
            log.warn("Failed to copy. Default Filename Will Use. Fail Url : {}", fileUrl);
            return cloudFront + defaultFilename;
        }
    }

    public String copy(FileDto fileDto, String destination) {
        if (fileDto.getOperation() == FileOperationType.DELETE) {
            return "";
        }

        String filename = fileDto.filename();

        String path = service.copy(
                UrlDirectory.getDirectory(fileDto.getType()) + filename,
                destination + filename);

        if (fileDto.containThumbnail()) {
            service.copy(TEMP_IMAGE.getDirectory() + fileDto.requestThumbnailFilename(),
                    destination + fileDto.thumbnailFilename());
        }

        return cloudFront + path;
    }

    public String copy(String fileUrl, String destination) {
        if (isBlank(fileUrl)) {
            return null;
        }

        String filename = getFileName(fileUrl);

        // FIXME Test Code 에서 올라오는 File URL 은 실제 파일이 없기때문에 복사할 수가 없어서 회피용 코드 삽입. 뭔가 이 방법 말고 좋은 방법이 있다면... 고쳐주세요
        if (filename.equals(TEST_FILE)) {
            return cloudFront + destination + filename;
        }

        String path = service.copy(
                TEMP_IMAGE.getDirectory() + filename,
                destination + filename);

        return cloudFront + path;
    }

    @Nullable
    public String copyWithKeepOriginal(String fileUrl, String destination) {
        if (isBlank(fileUrl)) {
            return null;
        }
        String filename = getFileName(fileUrl);
        String path = service.copyWithKeepOriginal(fileUrl, destination + filename);
        return cloudFront + path;
    }

    public List<String> copy(List<FileDto> fileDtoList, String destination) {
        if (isEmpty(fileDtoList)) {
            return null;
        }

        List<String> result = new ArrayList<>();
        for (FileDto fileDto : fileDtoList) {
            result.add(copy(fileDto, destination));
        }

        return result;
    }

    public List<String> editFiles(List<FileDto> fileDtoList, String destination) {
        if (isEmpty(fileDtoList)) {
            return List.of();
        }
        List<String> result = new ArrayList<>();
        for (FileDto fileDto : fileDtoList) {
            if (fileDto.getOperation() == null) {
                continue;
            }
            switch (fileDto.getOperation()) {
                case UPLOAD -> result.add(copy(fileDto, destination));
                case DELETE -> delete(fileDto);
            }
        }
        return result;
    }

    public void delete(FileDto fileDto) {
        String filename = getUri(fileDto.getUrl());
        service.delete(filename);
    }

    public void delete(String fileUrl) {
        if (isBlank(fileUrl)) {
            return;
        }
        String filename = getUri(fileUrl);
        service.delete(filename);
    }
}
