package com.jocoos.mybeautip.domain.notice.converter;

import com.amazonaws.util.CollectionUtils;
import com.jocoos.mybeautip.domain.member.converter.MemberConverter;
import com.jocoos.mybeautip.domain.notice.dto.NoticeListResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", uses = {MemberConverter.class})
public abstract class NoticeConverter {

    abstract public Notice converts(WriteNoticeRequest request);

    abstract public NoticeResponse converts(Notice notice);

    abstract public List<NoticeResponse> converts(List<Notice> noticeList);


    abstract public List<NoticeListResponse> convertsToListResponse(List<Notice> noticeList);

    public Page<NoticeResponse> convertsToResponsePage(Page<Notice> noticePage) {
        if (noticePage == null) {
            return new PageImpl<>(new ArrayList<>());
        }

        List<Notice> noticeList = noticePage.getContent();
        if (CollectionUtils.isNullOrEmpty(noticeList)) {
            return new PageImpl<>(new ArrayList<>());
        }

        List<NoticeResponse> responseList = this.converts(noticeList);
        return new PageImpl<>(responseList, noticePage.getPageable(), noticePage.getTotalElements());
    }

    public Page<NoticeListResponse> convertsToListResponsePage(Page<Notice> noticePage) {
        if (noticePage == null) {
            return new PageImpl<>(new ArrayList<>());
        }

        List<Notice> noticeList = noticePage.getContent();
        if (CollectionUtils.isNullOrEmpty(noticeList)) {
            return new PageImpl<>(new ArrayList<>());
        }

        List<NoticeListResponse> responseList = this.convertsToListResponse(noticeList);
        return new PageImpl<>(responseList, noticePage.getPageable(), noticePage.getTotalElements());
    }

    @Mappings({
            @Mapping(target = "type", constant = "IMAGE"),
            @Mapping(target = "file", source = "url", qualifiedByName = "urlToFile"),
            @Mapping(target = "notice", ignore = true)
    })
    abstract public NoticeFile converts(FileDto file);

    abstract public List<NoticeFile> convertToNoticeFiles(List<FileDto> files);

    @Mappings({
            @Mapping(target = "url", source = "file", qualifiedByName = "fileToUrl")
    })
    abstract public FileDto converts(NoticeFile entity);

    abstract public List<FileDto> convertToFileDto(List<NoticeFile> entity);

    @Named("urlToFile")
    public String urlToFile(String url) {
        return FileUtil.getFileName(url);
    }

    @Named("fileToUrl")
    public String fileToUrl(String file) {
        return ImageUrlConvertUtil.toUrl(file, UrlDirectory.NOTICE);
    }

    public FileDto convertsFileList(List<NoticeFile> entities) {
        if (CollectionUtils.isNullOrEmpty(entities)) {
            return null;
        }

        return entities.stream()
                .findFirst()
                .map(entity -> converts(entity))
                .orElse(null);
    }
}
