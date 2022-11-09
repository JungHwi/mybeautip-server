package com.jocoos.mybeautip.domain.community.persistence.domain;

import com.jocoos.mybeautip.domain.community.code.CommunityStatus;
import com.jocoos.mybeautip.domain.community.persistence.domain.vote.CommunityVote;
import com.jocoos.mybeautip.global.config.jpa.BaseEntity;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.member.Member;
import io.jsonwebtoken.lang.Collections;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.DRIP;
import static com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.VOTE;
import static com.jocoos.mybeautip.global.code.UrlDirectory.COMMUNITY;
import static com.jocoos.mybeautip.global.exception.ErrorCode.NOT_SUPPORTED_VOTE_NUM;
import static com.jocoos.mybeautip.global.util.FileUtil.getFilename;
import static com.jocoos.mybeautip.global.util.ImageUrlConvertUtil.toUrl;
import static org.springframework.util.CollectionUtils.isEmpty;
import static org.springframework.util.StringUtils.trimAllWhitespace;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "community")
public class Community extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private Long id;

    @Column(name = "category_id")
    private Long categoryId;

    @Column
    private Long eventId;

    @Column
    private Boolean isWin;

    @Column(name = "member_id")
    private Long memberId;

    @Column
    @Enumerated(EnumType.STRING)
    private CommunityStatus status;

    @Column
    private String title;

    @Column
    private String contents;

    @Column
    private int viewCount;

    @Column
    private int likeCount;

    @Column
    private int commentCount;

    @Column
    private int reportCount;

    @Column
    private Boolean isTopFix;

    @Column(columnDefinition = "DATETIME(3)")
    private ZonedDateTime sortedAt;

    @OneToMany(mappedBy = "community", fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<CommunityFile> communityFileList = new ArrayList<>();

    @OneToMany(mappedBy = "community", fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<CommunityVote> communityVoteList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", insertable = false, updatable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private CommunityCategory category;

    public Community delete() {
        if (!this.status.isDeletable()) {
            throw new BadRequestException("This status can not delete. This Community Status is " + this.status);
        }
        this.status = CommunityStatus.DELETE;
        return this;
    }

    public void addFile(String fileName) {
        CommunityFile communityFile = new CommunityFile(getFilename(fileName));
        communityFile.setCommunity(this);
        if (this.category.isCategoryType(VOTE)) {
            CommunityVote communityVote = new CommunityVote(this, communityFile);
            this.communityVoteList.add(communityVote);
        }
        this.getCommunityFileList().add(communityFile);
    }

    public void removeFile(String fileName) {
        if (this.category.isCategoryType(VOTE)) {
            this.communityVoteList.removeIf(vote -> vote.getCommunityFile().getFile().equals(getFilename(fileName)));
        }
        this.getCommunityFileList().removeIf(communityFile -> communityFile.getFile().equals(getFilename(fileName)));
    }

    public void valid() {
        switch (this.category.getType()) {
            case BLIND:
                validCommunity();
                validBlind();
                break;
            case DRIP:
                validCommunity();
                validDrip();
                break;
            case VOTE:
                validCommunity();
                validVote(this.communityVoteList.size());
                break;
            default:
                validCommunity();
        }
    }

    public void setContents(String contents) {
        validContents(contents);
        this.contents = contents;
    }

    private void validContents(String contents) {
        if (StringUtils.isBlank(contents) || contents.replace(StringUtils.SPACE, StringUtils.EMPTY).length() < 5) {
            throw new BadRequestException("Content length must be at least 5.");
        }
    }

    private void validCommunity() {
        validContents(this.contents);
    }

    private void validBlind() {
        if (StringUtils.isBlank(this.title) || this.title.length() < 5) {
            throw new BadRequestException("Community title of Blind Category must be over 5 length");
        }
    }

    private void validDrip() {
        if (this.eventId == null || this.eventId < 1) {
            throw new BadRequestException("Community of drip category needs event_id.");
        }
    }

    private void validVote(int voteNum) {
        if (voteNum != 0 && voteNum != 2) {
            throw new BadRequestException(NOT_SUPPORTED_VOTE_NUM.getDescription());
        }
    }

    public boolean isContentLongerThanOrSame(int length) {
        return trimAllWhitespace(this.contents).length() >= length;
    }

    public boolean isImageExist() {
        return !isEmpty(this.communityFileList);
    }

    public boolean isVoteAndIncludeFile(int fileNum) {
        if (!this.category.isCategoryType(VOTE)) {
            return false;
        }
        validVote(fileNum);
        return !Collections.isEmpty(this.communityVoteList) || !Collections.isEmpty(this.communityFileList);
    }

    @PostPersist
    public void postPersist() {
        if (communityFileList != null) {
            communityFileList.forEach(file -> file.setCommunity(this));
        }
    }

    public List<String> getFileUrls() {
        return communityFileList.stream()
                .map(file -> {
                    String url = toUrl(file.getFile(), COMMUNITY, id);
                    return Objects.requireNonNullElse(url, "");
                })
                .toList();
    }

    public boolean isWin() {
        if (isWin == null) {
            return false;
        }
        return isWin;
    }

    public boolean isTopFix() {
        if (isTopFix == null) {
            return false;
        }
        return isTopFix;
    }

    public boolean isVotesEmpty() {
        return CollectionUtils.isEmpty(communityVoteList);
    }

    public void win(boolean isWin) {
        if (!DRIP.equals(category.getType())) {
            throw new BadRequestException("Community Type Not Able TO Win");
        }
        this.isWin = isWin;
    }

    public void fix(boolean isFix) {
        this.isTopFix = isFix;
    }

    public void changeStatus(CommunityStatus status) {
        this.status = status;
    }
}
