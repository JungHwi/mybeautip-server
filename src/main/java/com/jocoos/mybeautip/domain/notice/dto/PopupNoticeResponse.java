package com.jocoos.mybeautip.domain.notice.dto;

import com.jocoos.mybeautip.domain.notice.code.NoticeLinkType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PopupNoticeResponse {

    private final Long id;

    private final String imageUrl;

    private final NoticeLinkType linkType;

    private final String parameter;

}
