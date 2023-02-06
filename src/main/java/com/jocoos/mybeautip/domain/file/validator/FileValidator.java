package com.jocoos.mybeautip.domain.file.validator;

import com.jocoos.mybeautip.domain.file.validator.annotation.ValidFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

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
        context.disableDefaultConstraintViolation();

        if (isExceedVideoCountLimit(value, context)) {
            return false;
        }

        return value.stream().noneMatch(file -> isTypeAndFileSizeInvalid(file, context));
    }

    private boolean isTypeAndFileSizeInvalid(MultipartFile file, ConstraintValidatorContext context) {
        return isExceedImageFileSizeLimit(file.getContentType(), file.getSize(), context);
    }

    private boolean isExceedVideoCountLimit(List<MultipartFile> files, ConstraintValidatorContext context) {
        long videoCount = files.stream()
                .map(MultipartFile::getContentType)
                .filter(type -> isContentTypeNonNullAndStartsWith(type, "video/"))
                .count();

        if (videoCount > videoLimitNum) {
            addConstraintViolation(context, "too many video");
            return true;
        }
        return false;
    }

    private boolean isExceedImageFileSizeLimit(String contentType, long fileSize, ConstraintValidatorContext context) {
        if (isContentTypeNonNullAndStartsWith(contentType, "image/") && isFileSizeBiggerThanLimit(fileSize)) {
            addConstraintViolation(context, String.format("limit is %d byte, request size is %d byte", imageFileSizeLimit.toBytes(), fileSize));
            return true;
        }
        return false;
    }

    private boolean isFileSizeBiggerThanLimit(long fileSize) {
        return imageFileSizeLimit.compareTo(DataSize.ofBytes(fileSize)) < 0;
    }

    private boolean isContentTypeNonNullAndStartsWith(String type, String prefix) {
        return type != null && type.startsWith(prefix);
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }

}
