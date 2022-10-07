package com.jocoos.mybeautip.domain.notice.converter;

import com.jocoos.mybeautip.domain.notice.dto.PopupNoticeResponse;
import com.jocoos.mybeautip.domain.notice.persistence.domain.PopupNotice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

import static com.jocoos.mybeautip.global.code.UrlDirectory.POPUP;
import static com.jocoos.mybeautip.global.util.ImageUrlConvertUtil.toUrl;

@Mapper(componentModel = "spring")
public interface PopupNoticeConverter {

    @Mapping(target = "imageUrl", source = "filename", qualifiedByName = "filename_to_url")
    @Mapping(target = "parameter", source = "linkArgument")
    PopupNoticeResponse convert(PopupNotice notice);

    List<PopupNoticeResponse> convert(List<PopupNotice> notices);

    @Named("filename_to_url")
    default String convertToUrl(String filename) {
        return toUrl(filename, POPUP);
    }
}
