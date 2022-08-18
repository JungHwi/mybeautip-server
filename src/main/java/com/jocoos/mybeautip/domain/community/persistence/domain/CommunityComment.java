package com.jocoos.mybeautip.domain.community.persistence.domain;

import com.jocoos.mybeautip.domain.community.code.CommunityStatus;
import com.jocoos.mybeautip.global.config.jpa.ModifiedAtBaseEntity;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "community_comment")
public class CommunityComment extends ModifiedAtBaseEntity {
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
        this.status = CommunityStatus.DELETE;
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
        if (StringUtils.isBlank(contents) || contents.length() < 5) {
            throw new BadRequestException("not_enough_contents", "Content length must be at least 5.");
        }
    }
}
