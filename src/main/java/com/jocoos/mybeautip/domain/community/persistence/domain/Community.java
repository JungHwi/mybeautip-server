package com.jocoos.mybeautip.domain.community.persistence.domain;

import com.jocoos.mybeautip.domain.community.code.CommunityStatus;
import com.jocoos.mybeautip.domain.community.persistence.domain.vote.CommunityVote;
import com.jocoos.mybeautip.domain.file.code.FileType;
import com.jocoos.mybeautip.domain.file.code.FileUrlDomain;
import com.jocoos.mybeautip.domain.member.code.Role;
import com.jocoos.mybeautip.global.config.jpa.BaseEntity;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.member.Member;
import io.jsonwebtoken.lang.Collections;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.DRIP;
import static com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.VOTE;
import static com.jocoos.mybeautip.domain.community.code.CommunityStatus.DELETE;
import static com.jocoos.mybeautip.global.exception.ErrorCode.ACCESS_DENIED;
import static com.jocoos.mybeautip.global.util.FileUtil.getFileName;
import static com.jocoos.mybeautip.global.util.ImageFileConvertUtil.toFileName;
import static com.jocoos.mybeautip.global.util.JsonNullableUtils.changeIfPresent;
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

    @OrderBy("id")
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
        this.status = DELETE;
        return this;
    }

    public void changeVideo(FileUrlDomain domain, String url) {
        communityFileList.stream()
                .filter(CommunityFile::isVideo)
                .findAny()
                .ifPresent(file -> file.change(domain, getFileName(url, domain)));
    }

    public CommunityFile getVideoUrl() {
        return communityFileList.stream()
                .filter(CommunityFile::isVideo)
                .findAny()
                .orElse(null);
    }

    public void addFile(FileType type, FileUrlDomain domain, String url) {
        CommunityFile communityFile = new CommunityFile(type, domain, getFileName(url, domain));
        communityFile.setCommunity(this);
        if (this.category.isCategoryType(VOTE)) {
            CommunityVote communityVote = new CommunityVote(this, communityFile);
            this.communityVoteList.add(communityVote);
        }
        this.getCommunityFileList().add(communityFile);
    }

    public void removeFile(String url) {
        if (this.category.isCategoryType(VOTE)) {
            this.communityVoteList.removeIf(vote -> vote.getCommunityFile().isUrlEqual(url));
        }
        this.getCommunityFileList().removeIf(communityFile -> communityFile.isUrlEqual(url));
    }

    public boolean isContentLongerThanOrSame(int length) {
        return trimAllWhitespace(this.contents).length() >= length;
    }

    public boolean isImageExist() {
        return !isEmpty(this.communityFileList);
    }

    public boolean isVoteAndIncludeFile() {
        if (!this.category.isCategoryType(VOTE)) {
            return false;
        }
        return !Collections.isEmpty(this.communityVoteList) || !Collections.isEmpty(this.communityFileList);
    }

    @PostPersist
    public void postPersist() {
        if (communityFileList != null) {
            communityFileList.forEach(file -> file.setCommunity(this));
        }
    }

    public boolean isWin() {
        if (isWin == null) {
            return false;
        }
        return isWin;
    }

    public boolean isVotesEmpty() {
        return isEmpty(communityVoteList);
    }

    public void win(boolean isWin) {
        if (!DRIP.equals(category.getType())) {
            throw new BadRequestException("Community Type Not Able TO Win");
        }
        this.isWin = isWin ? true : null;
    }

    public void fix(boolean isFix) {
        this.isTopFix = isFix ? true : null;
    }

    public void hide(boolean isHide) {
      this.status = status.hide(isHide);
    }

    public void edit(JsonNullable<String> title,
                     JsonNullable<String> contents) {
        changeIfPresent(title, this::editTitle);
        changeIfPresent(contents, this::editContents);
    }

    public void deleteAdminWrite() {
        validAdminWrite();
        delete();
    }

    private void editTitle(String title) {
        this.title = title;
    }

    private void editContents(String contents) {
        validContent(contents);
        this.contents = contents;
    }

    public void sortFilesByRequestIndex(List<String> sortedUrls) {
        if (communityFileList.size() == sortedUrls.size()) {
            IntStream.range(0, communityFileList.size())
                    .forEach(index -> replaceFileIfDiff(sortedUrls.get(index), communityFileList.get(index)));
        }
    }

    private void replaceFileIfDiff(String url, CommunityFile file) {
        if (!file.isUrlEqual(url)) {
            file.setFile(toFileName(url));
        }
    }

    private void validContent(String contents) {
        category.validContent(getMemberRole(), contents);
    }

    public void validReadAuth(Role role) {
        category.validReadAuth(role);
    }

    public void validWrite() {
        category.validWrite(this);
    }

    public void validAdminWrite() {
        if(!member.isAdmin()) {
            throw new BadRequestException(ACCESS_DENIED, "not a admin write community, community id - " + id);
        }
    }

    public Role getMemberRole() {
        return Role.from(member);
    }

    public int getFileSize() {
        return communityFileList.size();
    }

    public int getVoteSize() {
        return communityVoteList.size();
    }

    public void setVote() {
        if (VOTE.equals(category.getType())) {
            this.communityVoteList = communityFileList.stream()
                    .map(file -> new CommunityVote(this, file))
                    .toList();
        }
    }
}
