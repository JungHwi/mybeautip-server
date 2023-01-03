package com.jocoos.mybeautip.domain.community.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jocoos.mybeautip.domain.community.code.CommunityStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.CollectionUtils;

import java.time.ZonedDateTime;
import java.util.List;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

@Getter
@AllArgsConstructor
@Builder
public class AdminCommunityCommentResponse {

    private final Long id;
    private final CommunityStatus status;
    private final String contents;
    private final String fileUrl;
    private final int likeCount;
    private final int reportCount;
    private AdminMemberResponse member;
    private final List<AdminCommunityCommentResponse> children;

    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private final ZonedDateTime createdAt;

    @JsonIgnore
    private final Long parentId;

    public void blindMember() {
        member.blindExceptRole();
        if (!CollectionUtils.isEmpty(children)) {
            children.forEach(AdminCommunityCommentResponse::blindMember);
        }
    }
}
