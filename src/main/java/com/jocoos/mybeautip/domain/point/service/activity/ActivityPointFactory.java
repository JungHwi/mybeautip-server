package com.jocoos.mybeautip.domain.point.service.activity;

import com.jocoos.mybeautip.domain.point.code.ActivityPointType;
import com.jocoos.mybeautip.domain.point.service.activity.impl.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.jocoos.mybeautip.domain.point.code.ActivityPointType.*;

@RequiredArgsConstructor
@Component
public class ActivityPointFactory {

    private final InputAdditionalInfoPointValidator inputAdditionalInfoPointValidator;
    private final InputExtraInfoPointValidator inputExtraInfoPointValidator;

    private final WriteCommunityTypesPointValidator writeCommunityTypesPointValidator;
    private final WriteCommentPointValidator writeCommentPointValidator;

    private final GetLikeCommunityPointValidator getLikeCommunityPointValidator;
    private final GetLikeCommentPointValidator getLikeCommentPointValidator;
    private final GetLikeVideoPointValidator getLikeVideoPointValidator;

    private final VideoLikePointValidator videoLikePointValidator;
    private final VideoScrapPointValidator videoScrapPointValidator;

    private Map<ActivityPointType, ActivityPointValidator> map;

    @PostConstruct
    private void init() {
        Map<ActivityPointType, ActivityPointValidator> temp = new HashMap<>();
        temp.put(INPUT_ADDITIONAL_INFO, inputAdditionalInfoPointValidator);
        temp.put(INPUT_EXTRA_INFO, inputExtraInfoPointValidator);

        temp.put(WRITE_COMMUNITY, writeCommunityTypesPointValidator);
        temp.put(WRITE_PHOTO_COMMUNITY, writeCommunityTypesPointValidator);
        temp.put(WRITE_COMMENT, writeCommentPointValidator);

        temp.put(GET_LIKE_COMMUNITY, getLikeCommunityPointValidator);
        temp.put(GET_LIKE_COMMENT, getLikeCommentPointValidator);
        temp.put(GET_LIKE_VIDEO, getLikeVideoPointValidator);

        temp.put(VIDEO_LIKE, videoLikePointValidator);
        temp.put(VIDEO_SCRAP, videoScrapPointValidator);
        map = Collections.unmodifiableMap(temp);
    }

    public ActivityPointValidator getValidator(ActivityPointType type) {
        return map.get(type);
    }

    // 비슷한 사례 많아지면 리팩토링 예정
    public MultiTypeActivityPointValidator getValidator(Set<ActivityPointType> types) {
        if (WRITE_COMMUNITY_TYPES.equals(types)) {
            return writeCommunityTypesPointValidator;
        } return null;
    }
}
