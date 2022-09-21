package com.jocoos.mybeautip.domain.scrap.service;

import com.jocoos.mybeautip.domain.scrap.code.ScrapType;
import com.jocoos.mybeautip.domain.scrap.service.impl.CommunityScrapService;
import com.jocoos.mybeautip.domain.scrap.service.impl.VideoScrapService;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScrapTypeFactory {

    private final CommunityScrapService communityScrapService;
    private final VideoScrapService videoScrapService;

    public ScrapTypeService getScrapTypeService(ScrapType type) {
        switch (type) {
            case COMMUNITY:
                return communityScrapService;
            case VIDEO:
                return videoScrapService;
            default:
                throw new BadRequestException("not supported scrap type.");
        }
    }
}
