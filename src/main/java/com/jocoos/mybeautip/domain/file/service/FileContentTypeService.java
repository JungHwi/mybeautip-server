package com.jocoos.mybeautip.domain.file.service;

import com.jocoos.mybeautip.domain.file.code.FileSupportType;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.exception.ErrorCode;
import com.jocoos.mybeautip.global.exception.InternalServerException;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;

@RequiredArgsConstructor
@Service
public class FileContentTypeService {

    private final Tika tika;

    public String getContentType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || contentType.contains(APPLICATION_OCTET_STREAM_VALUE)) {
            contentType = getExtension(file).orElse(getContentTypeByTika(file));
        }
        validSupportedContentType(contentType);
        return contentType;
    }

    private Optional<String> getExtension(MultipartFile file) {
        final String dot = ".";
        return Optional.ofNullable(file.getOriginalFilename())
                .filter(filename -> filename.contains(dot))
                .map(filename -> filename.substring(filename.lastIndexOf(dot) + 1));
    }

    private String getContentTypeByTika(MultipartFile file) {
        try(InputStream inputStream = file.getInputStream()) {
            return tika.detect(inputStream);
        } catch (IOException e) {
            throw new InternalServerException(ErrorCode.FILE_IO_EXCEPTION, e);
        }
    }

    private void validSupportedContentType(String contentType) {
        if (!FileSupportType.isSupport(contentType)) {
            throw new BadRequestException("unsupported file content type " + contentType);
        }
    }
}
