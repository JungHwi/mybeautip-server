package com.jocoos.mybeautip.domain.member.converter;

import com.jocoos.mybeautip.domain.member.dto.MemberInvitationInfoResponse;
import com.jocoos.mybeautip.domain.member.persistence.domain.MemberInvitationInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import static com.jocoos.mybeautip.global.code.UrlDirectory.SHARE;
import static com.jocoos.mybeautip.global.util.ImageUrlConvertUtil.toUrl;

@Mapper(componentModel = "spring")
public interface MemberInvitationInfoConverter {

    String CONVERT_TO_URL = "convertToUrl";

    @Mappings({
            @Mapping(source = "shareSquareImageFilename", target = "shareSquareImageUrl", qualifiedByName = CONVERT_TO_URL),
            @Mapping(source = "shareRectangleImageFilename", target = "shareRectangleImageUrl", qualifiedByName = CONVERT_TO_URL)
    })
    MemberInvitationInfoResponse convertToResponse(MemberInvitationInfo info);

    @Named(CONVERT_TO_URL)
    default String convertToUrl(String filename) {
        return toUrl(filename, SHARE);
    }
}
