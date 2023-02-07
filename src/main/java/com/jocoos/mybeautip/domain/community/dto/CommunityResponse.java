package com.jocoos.mybeautip.domain.community.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import com.jocoos.mybeautip.domain.community.code.CommunityStatus;
import com.jocoos.mybeautip.domain.community.vo.CommunityRelationInfo;
import com.jocoos.mybeautip.global.dto.FileDto;
import com.jocoos.mybeautip.global.util.date.ZonedDateTimeUtil;
import com.jocoos.mybeautip.global.wrapper.CursorInterface;
import com.jocoos.mybeautip.member.Member;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.BLIND;
import static com.jocoos.mybeautip.domain.file.code.FileType.IMAGE;
import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;
import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_MILLI_FORMAT;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommunityResponse implements CursorInterface {

    private Long id;

    private Boolean isWin;

    private CommunityStatus status;

    private Long eventId;

    private String eventTitle;
    private String title;

    private String contents;

    private List<FileDto> files;
    private List<String> fileUrl;
    private List<VoteResponse> votes;

    private Integer viewCount;

    private Integer likeCount;

    private Integer reportCount;

    private Integer commentCount;

    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private ZonedDateTime createdAt;

    private CommunityRelationInfo relationInfo;

    private MemberResponse member;

    private CommunityCategoryResponse category;

    @JsonIgnore
    private ZonedDateTime sortedAt;

    @Override
    @JsonIgnore
    public String getCursor() {
        return ZonedDateTimeUtil.toString(sortedAt, ZONE_DATE_TIME_MILLI_FORMAT);
    }

    public CommunityResponse setRelationInfo(CommunityRelationInfo relationInfo) {
        return setRelationInfo(null, relationInfo);
    }

    public CommunityResponse setRelationInfo(Member member, CommunityRelationInfo relationInfo) {
        this.relationInfo = relationInfo;

        // FIXME 관계나 상태에 따라 Title / Contents 변경. 어디다 치워 버리고 싶다.
        if (relationInfo.getIsBlock() && this.category.getType() != BLIND) {
            this.contents = "차단된 사용자의 글이에요.";
            this.files = new ArrayList<>();
            this.votes = new ArrayList<>();
        } else if (this.reportCount >= 3) {
            this.contents = "커뮤니티 운영방침에 따라 블라인드 되었어요.";
            this.files = new ArrayList<>();
            this.votes = new ArrayList<>();
            if (this.category.getType() == BLIND) {
                this.title = "커뮤니티 운영방침에 따라 블라인드 되었어요.";
            }
        } else if (relationInfo.getIsReport()) {
            this.contents = "신고 접수 된 글이에요.";
            this.files = new ArrayList<>();
            this.votes = new ArrayList<>();
            if (this.category.getType() == BLIND) {
                this.title = "신고 접수 된 글이에요.";
            }
        } else if (this.status == CommunityStatus.DELETE) {
            this.contents = "삭제된 게시물이에요.";
            this.files = new ArrayList<>();
            this.votes = new ArrayList<>();
            if (this.category.getType() == BLIND) {
                this.title = "삭제된 게시물이에요.";
            }
        }

        if (this.category.getType() == BLIND && (member == null || !this.member.getId().equals(member.getId()))) {
            this.member.blind();
        }

        return this;
    }

    public void userVote(Long userVoted) {
        if (votes != null) {
            setIsVotedAndCount(userVoted);
        }
    }

    private void setIsVotedAndCount(Long userVoted) {
        if (userVoted == null) {
            for (VoteResponse vote : votes) {
                vote.setCountZero();
                vote.userVoted(false);
            }
        } else {
            this.votes.forEach(vote -> vote.userVoted(userVoted.equals(vote.getId())));
        }
    }

    public boolean isCategoryType(CommunityCategoryType type) {
        return Objects.equals(this.category.getType(), type);
    }

    public void listBlindContent() {
        if (BLIND.equals(this.category.getType())) {
            this.contents = null;
        }
    }

    public void toV1() {
        this.fileUrl = files.stream()
                .map(fileDto -> {
                    if (fileDto.getType().equals(IMAGE)) return fileDto.getUrl();
                    else return fileDto.getThumbnailUrl();
                })
                .toList();
        this.files = null;
    }
}
