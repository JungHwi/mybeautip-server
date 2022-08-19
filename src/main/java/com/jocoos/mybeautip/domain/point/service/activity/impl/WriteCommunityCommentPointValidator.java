package com.jocoos.mybeautip.domain.point.service.activity.impl;

import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityComment;
import com.jocoos.mybeautip.domain.point.service.activity.ActivityPointValidator;
import com.jocoos.mybeautip.domain.point.util.DateValidator;
import com.jocoos.mybeautip.domain.point.util.ValidObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.jocoos.mybeautip.domain.point.code.ActivityPointType.WRITE_COMMENT_TYPES;
import static com.jocoos.mybeautip.domain.point.util.DateRestriction.day;

@RequiredArgsConstructor
@Component
public class WriteCommunityCommentPointValidator implements ActivityPointValidator {

    private final DateValidator dateValidator;
    private static final int DATE_LIMIT_NUM = 10;
    private static final int CONTENT_VALID_LENGTH = 5;

    @Override
    public boolean valid(ValidObject validObject) {
        return contentValid(validObject.getDomain()) &&
                dateValidator.valid(WRITE_COMMENT_TYPES, day(DATE_LIMIT_NUM), validObject.getReceiveMember());
    }

    private boolean contentValid(Object domain) {
        CommunityComment comment = (CommunityComment) domain;
        return comment.isCommentSameOrLongerThan(CONTENT_VALID_LENGTH);
    }
}
