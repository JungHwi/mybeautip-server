package com.jocoos.mybeautip.domain.point.service.activity.impl;

import com.jocoos.mybeautip.domain.point.service.activity.ActivityPointValidator;
import com.jocoos.mybeautip.domain.point.valid.DateValidator;
import com.jocoos.mybeautip.domain.point.service.activity.ValidObject;
import com.jocoos.mybeautip.member.comment.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.jocoos.mybeautip.domain.point.code.ActivityPointType.WRITE_COMMENT_TYPES;
import static com.jocoos.mybeautip.domain.point.valid.DateRestriction.day;

@RequiredArgsConstructor
@Component
public class WriteVideoCommentPointValidator implements ActivityPointValidator {

    private final DateValidator dateValidator;

    @Value("${mybeautip.activity-point.comment-write-date-limit}")
    private int dateLimitNum;

    @Value("${mybeautip.activity-point.comment-valid-length}")
    private int contentValidLength;

    @Override
    public boolean valid(ValidObject validObject) {
        return contentValid(validObject.getDomain()) &&
                dateValidator.valid(WRITE_COMMENT_TYPES, day(this.dateLimitNum), validObject.getReceiveMember());
    }

    private boolean contentValid(Object domain) {
        Comment comment = (Comment) domain;
        return comment.isCommentSameOrLongerThan(this.contentValidLength);
    }
}
