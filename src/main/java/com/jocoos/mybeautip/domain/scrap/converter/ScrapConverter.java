package com.jocoos.mybeautip.domain.scrap.converter;

import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.scrap.dto.ScrapRequest;
import com.jocoos.mybeautip.domain.scrap.dto.ScrapResponse;
import com.jocoos.mybeautip.domain.scrap.persistence.domain.Scrap;
import com.jocoos.mybeautip.video.Video;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ScrapConverter {

    public abstract Scrap convert(ScrapRequest request);

    public abstract ScrapResponse convert(Scrap scrap);

    public List<ScrapResponse> convertVideoScrap(List<Scrap> scrapList, List<Video> videoList) {


        return null;
    }

    public List<ScrapResponse> convertCommunityScrap(List<Scrap> scrapList, List<Community> communityList) {
        return null;
    }
}
