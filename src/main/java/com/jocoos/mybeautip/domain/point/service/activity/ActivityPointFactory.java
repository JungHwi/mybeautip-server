package com.jocoos.mybeautip.domain.point.service.activity;

import com.jocoos.mybeautip.domain.point.code.ActivityPointType;
import com.jocoos.mybeautip.domain.point.service.activity.impl.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import static com.jocoos.mybeautip.domain.point.code.ActivityPointType.*;

@RequiredArgsConstructor
@Component
public class ActivityPointFactory {

    private final InputAdditionalInfoPointValidator inputAdditionalInfoPointValidator;
    private final InputExtraInfoPointValidator inputExtraInfoPointValidator;

    private final WriteCommunityTypesPointValidator writeCommunityTypesPointValidator;
    private final WriteCommunityCommentPointValidator writeCommunityCommentPointValidator;
    private final WriteVideoCommentPointValidator writeVideoCommentPointValidator;

    private final GetLikeCommunityPointValidator getLikeCommunityPointValidator;

    private final GetLikeCommunityCommentPointValidator getLikeCommunityCommentPointValidator;
    private final GetLikeVideoCommentPointValidator getLikeVideoCommentPointValidator;
    private final GetLikeVideoPointValidator getLikeVideoPointValidator;

    private final VideoLikePointValidator videoLikePointValidator;
    private final VideoScrapPointValidator videoScrapPointValidator;

    private Map<ActivityPointType, ActivityPointValidator> map;

    @PostConstruct
    private void init() {
        Map<ActivityPointType, ActivityPointValidator> temp = new EnumMap<>(ActivityPointType.class);
        temp.put(INPUT_ADDITIONAL_INFO, inputAdditionalInfoPointValidator);
        temp.put(INPUT_EXTRA_INFO, inputExtraInfoPointValidator);

        temp.put(WRITE_COMMUNITY, writeCommunityTypesPointValidator);
        temp.put(WRITE_PHOTO_COMMUNITY, writeCommunityTypesPointValidator);
        temp.put(WRITE_COMMUNITY_COMMENT, writeCommunityCommentPointValidator);
        temp.put(WRITE_VIDEO_COMMENT, writeVideoCommentPointValidator);

        temp.put(GET_LIKE_COMMUNITY, getLikeCommunityPointValidator);
        temp.put(GET_LIKE_COMMUNITY_COMMENT, getLikeCommunityCommentPointValidator);
        temp.put(GET_LIKE_VIDEO_COMMENT, getLikeVideoCommentPointValidator);
        temp.put(GET_LIKE_VIDEO, getLikeVideoPointValidator);

        temp.put(VIDEO_LIKE, videoLikePointValidator);
        temp.put(VIDEO_SCRAP, videoScrapPointValidator);
        map = Collections.unmodifiableMap(temp);
    }

    public ActivityPointValidator getValidator(ActivityPointType type) {
        ActivityPointValidator validator = map.get(type);
        nullCheck(type, validator);
        return validator;
    }

    private void nullCheck(ActivityPointType type, ActivityPointValidator validator) {
        if (validator == null) {
            // TODO EXCEPTION 정의
            throw new RuntimeException("activity point type not correct : " + type);
        }
    }

    // 비슷한 사례 많아지면 리팩토링 예정
    public MultiTypeActivityPointValidator getValidator(Set<ActivityPointType> types) {
        if (WRITE_COMMUNITY_TYPES.equals(types)) {
            return writeCommunityTypesPointValidator;
        } return null;
    }
}
