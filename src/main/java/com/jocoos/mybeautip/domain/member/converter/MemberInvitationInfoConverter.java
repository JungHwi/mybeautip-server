package com.jocoos.mybeautip.domain.member.converter;

import com.jocoos.mybeautip.domain.member.dto.MemberInvitationInfoResponse;
import com.jocoos.mybeautip.domain.member.persistence.domain.MemberInvitationInfo;
import com.jocoos.mybeautip.global.util.ImageUrlConvertUtil;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import static com.jocoos.mybeautip.global.code.UrlDirectory.SHARE;
import static com.jocoos.mybeautip.global.util.ImageUrlConvertUtil.toUrl;

@Mapper(componentModel = "spring")
public interface MemberInvitationInfoConverter {

    String CONVERT_TO_URL = "convertToUrl";

    @Mapping(source = "shareSquareImageUrl", target = "shareSquareImageUrl", qualifiedByName = CONVERT_TO_URL)
    @Mapping(source = "shareRectangleImageUrl", target = "shareRectangleImageUrl", qualifiedByName = CONVERT_TO_URL)
    MemberInvitationInfoResponse convertToResponse(MemberInvitationInfo info);

    @Named(CONVERT_TO_URL)
    default String convertToUrl(String filename) {
        return toUrl(filename, SHARE);
    }
}
