package com.jocoos.mybeautip.domain.member.persistence.domain;

import com.jocoos.mybeautip.global.config.jpa.BaseEntity;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.member.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static com.jocoos.mybeautip.global.exception.ErrorCode.NOT_A_WRITER;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MemberMemo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id")
    private Member target;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private Member createdBy;

    public MemberMemo(String content, Member target, Member createdBy) {
        this.content = content;
        this.target = target;
        this.createdBy = createdBy;
    }

    public void edit(String editContent, Member editedBy) {
        validSameWriter(editedBy);
        this.content = editContent;
    }

    public void validSameWriter(Member editedBy) {
        if (!createdBy.getId().equals(editedBy.getId())) {
            throw new BadRequestException(NOT_A_WRITER);
        }
    }
}
