package com.jocoos.mybeautip.domain.community.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import com.jocoos.mybeautip.domain.community.code.CommunityStatus;
import com.jocoos.mybeautip.domain.community.vo.CommunityRelationInfo;
import com.jocoos.mybeautip.global.wrapper.CursorInterface;
import com.jocoos.mybeautip.member.Member;
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

    private Long categoryId;

    private Long communityId;

    private Long parentId;

    private CommunityStatus status;

    private String contents;

    private String fileUrl;

    private Integer likeCount;

    private Integer commentCount;

    private Integer reportCount;

    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private ZonedDateTime createdAt;

    private CommunityRelationInfo relationInfo;

    private MemberResponse member;

    @Override
    @JsonIgnore
    public String getCursor() {
        return String.valueOf(this.id);
    }

    public CommunityCommentResponse setRelationInfo(CommunityRelationInfo relationInfo, CommunityCategoryType categoryType) {
        return setRelationInfo(null, relationInfo, categoryType);
    }

    public CommunityCommentResponse setRelationInfo(Member member, CommunityRelationInfo relationInfo, CommunityCategoryType categoryType) {
        this.relationInfo = relationInfo;

        // FIXME 관계나 상태에 따라 Title / Contents 변경. 어디다 치워 버리고 싶다.
        if (relationInfo.getIsBlock() && categoryType != CommunityCategoryType.BLIND) {
            this.contents = "차단된 사용자의 글이에요.";
            this.fileUrl = null;
        } else if (this.reportCount >= 3) {
            this.contents = "커뮤니티 운영방침에 따라 블라인드 되었어요.";
        } else if (relationInfo.getIsReport()) {
            this.contents = "신고 접수 된 글이에요.";
            this.fileUrl = null;
        } else if (this.status == CommunityStatus.DELETE) {
            this.contents = "삭제된 게시물이에요.";
            this.fileUrl = null;
        }

        if (categoryType == CommunityCategoryType.BLIND && (member == null || !this.member.getId().equals(member.getId()))) {
            this.member.blind();
        }

        return this;
    }
}
