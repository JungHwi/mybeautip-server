package com.jocoos.mybeautip.domain.community.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jocoos.mybeautip.domain.community.code.CommunityStatus;
import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityFile;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import org.springframework.util.CollectionUtils;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.BLIND;
import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

@Getter
public class AdminCommunityResponse {

    private final Long id;
    private final String title;
    private final String eventTitle;
    private final CommunityStatus status;
    private final String contents;
    private final int viewCount;
    private final int likeCount;
    private final int commentCount;
    private final int reportCount;
    private final Boolean isWin;
    private final Boolean isTopFix;
    private final CommunityCategoryResponse category;
    private final MemberResponse member;
    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT) private final ZonedDateTime createdAt;
    private List<String> fileUrl;
    private List<VoteResponse> votes;

    @QueryProjection
    public AdminCommunityResponse(Community community,
                                  CommunityCategoryResponse category,
                                  MemberResponse member,
                                  String eventTitle) {
        this.id = community.getId();
        this.status = community.getStatus();
        this.title = eventTitle == null ? community.getTitle() : null;
        this.eventTitle = eventTitle;
        this.contents = community.getContents();
        this.viewCount = community.getViewCount();
        this.likeCount = community.getLikeCount();
        this.commentCount = community.getCommentCount();
        this.reportCount = community.getReportCount();
        this.isWin = community.getIsWin();
        this.isTopFix = community.getIsTopFix();
        this.createdAt = community.getCreatedAt();
        this.category = category;
        this.member = category.getType() == BLIND ? null : member;
    }

    public AdminCommunityResponse(Community community,
                                  List<CommunityFile> files,
                                  CommunityCategoryResponse category,
                                  MemberResponse member,
                                  String eventTitle) {
        this(community, category, member, eventTitle);
        this.fileUrl = getFileUrl(files);
    }

    public void setVotes(Map<Long, List<VoteResponse>> votesMap) {
        setVotesAndClearFileUrls(votesMap.get(id));
    }

    public void setVotesAndClearFileUrls(List<VoteResponse> votes) {
        if (CollectionUtils.isEmpty(votes)) {
            return;
        }
        this.votes = votes;
        this.fileUrl = new ArrayList<>();
    }

    public List<String> getFileUrl(List<CommunityFile> files) {
        if (CollectionUtils.isEmpty(files)) {
            return Collections.emptyList();
        }
        return files.stream()
                .map(CommunityFile::getFileUrl)
                .toList();
    }

    public void setFileUrls(Map<Long, List<CommunityFile>> fileMap) {
        this.fileUrl = getFileUrl(fileMap.getOrDefault(id, new ArrayList<>()));
    }
}
