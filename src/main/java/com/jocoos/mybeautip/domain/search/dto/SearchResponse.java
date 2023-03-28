package com.jocoos.mybeautip.domain.search.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jocoos.mybeautip.domain.community.dto.CommunityResponse;
import com.jocoos.mybeautip.domain.search.code.SearchType;
import com.jocoos.mybeautip.global.wrapper.CursorInterface;
import com.jocoos.mybeautip.global.wrapper.CursorResultResponse;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jocoos.mybeautip.domain.search.code.SearchType.*;

@Getter
public class SearchResponse<T extends CursorInterface> extends CursorResultResponse<T> {

    @JsonIgnore
    private final Map<String, Object> maps = new HashMap<>();
    private final Long count;

    public SearchResponse(List<T> content, Long count) {
        super(content);
        this.count = count;
    }

    @JsonAnyGetter
    public Map<String, Object> getSearch() {
        return new HashMap<>(maps);
    }

    public SearchResponse<T> contentJsonNameTo(SearchType type) {
        maps.put(type.name().toLowerCase(), super.getContent());
        super.contentToNull();
        return this;
    }

    public SearchResponse<T> toV1() {
        Object o = maps.get(COMMUNITY.name().toLowerCase());
        if (o instanceof List<?> list) {
            List<CommunityResponse> communityResponses = (List<CommunityResponse>) list;
            for (CommunityResponse communityResponse : communityResponses) {
                communityResponse.toV1();
            }
        }
        return this;
    }
}
