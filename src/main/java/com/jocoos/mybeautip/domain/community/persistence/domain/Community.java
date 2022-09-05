package com.jocoos.mybeautip.domain.community.persistence.domain;

import com.jocoos.mybeautip.domain.community.code.CommunityStatus;
import com.jocoos.mybeautip.global.config.jpa.BaseEntity;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;

import static com.jocoos.mybeautip.global.util.FileUtil.getFilename;
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

    @Column(columnDefinition = "DATETIME(3)")
    private ZonedDateTime sortedAt;

    @OneToMany(mappedBy = "community", fetch = FetchType.EAGER, cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<CommunityFile> communityFileList;

    @ManyToOne
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
        this.getCommunityFileList().add(communityFile);
    }

    public void removeFile(String fileName) {
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
            throw new BadRequestException("not_enough_contents", "Content length must be at least 5.");
        }
    }

    private void validCommunity() {
        validContents(this.contents);
    }

    private void validBlind() {
        if (StringUtils.isBlank(this.title) || this.title.length() < 5) {
            throw new BadRequestException("not_enough_title", "Community title of Blind Category must be over 5 length");
        }
    }

    private void validDrip() {
        if (this.eventId == null || this.eventId < 1) {
            throw new BadRequestException("need_event_id", "Community of drip category needs event_id.");
        }
    }

    public boolean isContentLongerThanOrSame(int length) {
        return trimAllWhitespace(this.contents).length() >= length;
    }

    public boolean isImageExist() {
        return !CollectionUtils.isEmpty(this.communityFileList);
    }

    @PostPersist
    public void postPersist() {
        if (communityFileList != null) {
            communityFileList.forEach(file -> file.setCommunity(this));
        }
    }
}
