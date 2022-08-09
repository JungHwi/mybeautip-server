package com.jocoos.mybeautip.domain.community.persistence.domain;

import com.jocoos.mybeautip.domain.community.code.CommunityStatus;
import com.jocoos.mybeautip.global.config.jpa.ModifiedAtBaseEntity;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "community")
public class Community extends ModifiedAtBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private Long id;

    @Column(name = "category_id")
    private Long categoryId;

    @Column
    private Long eventId;

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
    private ZonedDateTime sortedAt;

    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL)
    private List<CommunityFile> communityFileList;

    @ManyToOne
    @JoinColumn(name = "member_id", insertable = false, updatable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private CommunityCategory category;

    @PostPersist
    public void postPersist() {
        if (communityFileList != null) {
            communityFileList.forEach(file -> file.setCommunity(this));
        }
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

    private void validCommunity() {
        if (this.contents.length() < 5) {
            throw new BadRequestException("not_enough_contents", "Contents must be over 5 length");
        }
    }

    private void validBlind() {
        if (this.title.length() < 5) {
            throw new BadRequestException("not_enough_title", "Community title of Blind Category must be over 5 length");
        }
    }

    private void validDrip() {
        if (this.eventId == null || this.eventId < 1) {
            throw new BadRequestException("need_event_id", "Community of drip category needs event_id.");
        }
    }
}