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
    private String memo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private Member createdBy;

    public void update(String memo) {
        this.memo = memo;
    }

    public MemberMemo(String memo, Member member, Member createdBy) {
        this.memo = memo;
        this.member = member;
        this.createdBy = createdBy;
    }

    public void edit(String editMemo, Member editedBy) {
        validSameWriterEditor(editedBy);
        this.memo = editMemo;
    }

    private void validSameWriterEditor(Member editedBy) {
        if (!createdBy.equals(editedBy)) {
            throw new BadRequestException(NOT_A_WRITER);
        }
    }
}
