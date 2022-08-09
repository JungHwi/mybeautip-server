package com.jocoos.mybeautip.domain.community.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import com.jocoos.mybeautip.domain.community.code.CommunityStatus;
import com.jocoos.mybeautip.domain.community.vo.CommunityRelationInfo;
import com.jocoos.mybeautip.domain.member.code.MemberStatus;
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

    public CommunityResponse setRelationInfo(CommunityRelationInfo relationInfo) {
        this.relationInfo = relationInfo;

        // FIXME 관계나 상태에 따라 Title / Contents 변경. 어디다 치워 버리고 싶다.
        if (relationInfo.getIsBlock()) {
            this.contents = "차단된 사용자의 글이에요.";
            return this;
        } else if (this.reportCount > 3) {
            this.contents = "";
            if (this.category.getType() == CommunityCategoryType.BLIND) {
                this.title = "";
            }
            return this;
        } else if (this.member.getStatus() == MemberStatus.WITHDRAWAL) {
            this.contents = "";
            if (this.category.getType() == CommunityCategoryType.BLIND) {
                this.title = "";
            }
            return this;
        } else if (this.status == CommunityStatus.DELETE) {
            this.contents = "";
            if (this.category.getType() == CommunityCategoryType.BLIND) {
                this.title = "";
            }
            return this;
        }

        return this;
    }
}
