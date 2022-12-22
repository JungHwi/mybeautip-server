package com.jocoos.mybeautip.domain.file;

import com.jocoos.mybeautip.domain.file.code.FileSupportType;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.exception.ContentTypeUnsupportedException;
import com.jocoos.mybeautip.global.exception.FileSizeExceedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

import static com.jocoos.mybeautip.global.exception.ErrorCode.TOO_MANY_FILE;

public class FileValidator implements ConstraintValidator<ValidFile, List<MultipartFile>> {

    private final DataSize imageFileSizeLimit;
    private final int videoLimitNum;

    public FileValidator(@Value("${mybeautip.image-file-limit-mb}") int imageFileSizeLimitMB,
                         @Value("${mybeautip.video-file-count-limit}") int videoLimitNum) {
        this.imageFileSizeLimit = DataSize.ofMegabytes(imageFileSizeLimitMB);
        this.videoLimitNum = videoLimitNum;
    }

    @Override
    public boolean isValid(List<MultipartFile> value, ConstraintValidatorContext context) {
        validFileNum(value);
        for (MultipartFile file : value) {
            validSupportedContentType(file.getContentType());
            validImageFileSize(file.getContentType(), file.getSize());
        }
        return true;
    }

    private void validFileNum(List<MultipartFile> files) {
        validAllFileNum(files.size());
        validVideoNum(files);
    }

    private void validSupportedContentType(String contentType) {
        if (!FileSupportType.isSupport(contentType)) {
            throw new ContentTypeUnsupportedException(contentType);
        }
    }

    private void validImageFileSize(String contentType, long fileSize) {
        if (isContentTypeNonNullAndStartsWith(contentType, "image/") && isFileSizeBiggerThanLimit(fileSize)) {
            throw new FileSizeExceedException(imageFileSizeLimit.toBytes(), fileSize);
        }
    }

    private void validAllFileNum(int fileCount) {
        if (fileCount > 5) {
            throw new BadRequestException(TOO_MANY_FILE);
        }
    }

    private void validVideoNum(List<MultipartFile> files) {
        long videoCount = files.stream()
                .map(MultipartFile::getContentType)
                .filter(type -> isContentTypeNonNullAndStartsWith(type, "video/"))
                .count();

        if (videoCount > videoLimitNum) {
            throw new BadRequestException(TOO_MANY_FILE, "too many video");
        }
    }

    private boolean isContentTypeNonNullAndStartsWith(String type, String prefix) {
        return type != null && type.startsWith(prefix);
    }

    private boolean isFileSizeBiggerThanLimit(long fileSize) {
        return imageFileSizeLimit.compareTo(DataSize.ofBytes(fileSize)) < 0;
    }

}
