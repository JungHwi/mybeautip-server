package com.jocoos.mybeautip.domain.community.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jocoos.mybeautip.domain.community.code.CommunityStatus;
import com.jocoos.mybeautip.domain.community.vo.CommunityRelationInfo;
import com.jocoos.mybeautip.global.util.ZonedDateTimeUtil;
import com.jocoos.mybeautip.global.wrapper.CursorInterface;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.List;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommunityResponse implements CursorInterface {

    private Long id;

    private CommunityStatus status;

    private String title;

    private String contents;

    private List<String> fileUrl;

    private Integer viewCount;

    private Integer likeCount;

    private Integer reportCount;

    private Integer commentCount;

    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private ZonedDateTime createdAt;

    private CommunityRelationInfo relationInfo;

    private CommunityMemberResponse member;

    private CommunityCategoryResponse category;

    @JsonIgnore
    private ZonedDateTime sortedAt;

    @Override
    @JsonIgnore
    public String getCursor() {
        return ZonedDateTimeUtil.toString(sortedAt);
    }
}
