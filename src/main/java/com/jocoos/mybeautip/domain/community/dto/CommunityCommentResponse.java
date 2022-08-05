package com.jocoos.mybeautip.domain.community.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jocoos.mybeautip.domain.community.code.CommunityStatus;
import com.jocoos.mybeautip.domain.community.vo.CommunityRelationInfo;
import com.jocoos.mybeautip.global.wrapper.CursorInterface;
import lombok.*;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommunityCommentResponse implements CursorInterface {

    private Long id;

    private CommunityStatus status;

    private String contents;

    private Integer likeCount;

    private Integer replyCount;

    private Integer reportCount;

    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private ZonedDateTime createdAt;

    private CommunityRelationInfo relationInfo;

    private CommunityMemberResponse member;

    @Override
    @JsonIgnore
    public String getCursor() {
        return String.valueOf(this.id);
    }
}
