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

import javax.persistence.*;

import static com.jocoos.mybeautip.domain.community.code.CommunityStatus.DELETE;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "community_comment")
public class CommunityComment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private Long id;

    @Column
    private long categoryId;

    @Column
    private long communityId;

    @Column(name = "member_id")
    private long memberId;

    @Column
    private Long parentId;

    @Column
    @Enumerated(EnumType.STRING)
    private CommunityStatus status;

    @Column
    private String contents;

    @Column
    private int likeCount;

    @Column
    private int commentCount;

    @Column
    private int reportCount;

    @ManyToOne
    @JoinColumn(name = "member_id", insertable = false, updatable = false)
    private Member member;

    public CommunityComment delete() {
        if (!this.status.isDeletable()) {
            throw new BadRequestException("This status can not delete. This Community Status is " + this.status);
        }
        this.status = DELETE;
        return this;
    }

    public void setContents(String contents) {
        validContents(contents);

        this.contents = contents;
    }

    public void valid() {
        validContents(this.contents);
    }

    private void validContents(String contents) {
        if (StringUtils.isBlank(contents)) {
            throw new BadRequestException("Content must not be empty.");
        }
    }

    public boolean isCommentSameOrLongerThan(int length) {
        return this.contents.length() >= length;
    }

    public boolean isParent() {
        return parentId == null;
    }

    public boolean isChild() {
        return parentId != null;
    }

    public boolean eqStatus(CommunityStatus status) {
        return this.status == status;
    }

}
