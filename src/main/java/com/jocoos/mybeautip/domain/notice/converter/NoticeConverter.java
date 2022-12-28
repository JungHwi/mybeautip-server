package com.jocoos.mybeautip.domain.notice.converter;

import com.jocoos.mybeautip.domain.notice.dto.NoticeResponse;
import com.jocoos.mybeautip.domain.notice.dto.WriteNoticeRequest;
import com.jocoos.mybeautip.domain.notice.persistence.domain.Notice;
import com.jocoos.mybeautip.domain.notice.persistence.domain.NoticeFile;
import com.jocoos.mybeautip.global.code.UrlDirectory;
import com.jocoos.mybeautip.global.dto.FileDto;
import com.jocoos.mybeautip.global.util.FileUtil;
import com.jocoos.mybeautip.global.util.ImageUrlConvertUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NoticeConverter {

    @Mappings({
            @Mapping(target = "status", constant = "ACTIVE")
    })
    Notice converts(WriteNoticeRequest request);

    NoticeResponse converts(Notice notice);

    @Mappings({
            @Mapping(target = "type", constant = "IMAGE"),
            @Mapping(target = "file", source = "url", qualifiedByName = "urlToFile"),
            @Mapping(target = "notice", ignore = true)
    })
    NoticeFile converts(FileDto file);

    List<NoticeFile> convertToNoticeFiles(List<FileDto> files);

    FileDto converts(NoticeFile entity);

    List<FileDto> convertToFileDto(List<NoticeFile> entity);

    @Named("urlToFile")
    default String urlToFile(String url) {
        return FileUtil.getFileName(url);
    }

    @Named("fileToUrl")
    default String fileToUrl(String file) {
        return ImageUrlConvertUtil.toUrl(file, UrlDirectory.NOTICE);
    }
}
