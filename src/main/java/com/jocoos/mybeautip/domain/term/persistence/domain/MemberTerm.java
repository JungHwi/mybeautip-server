package com.jocoos.mybeautip.domain.term.persistence.domain;

import com.jocoos.mybeautip.global.config.jpa.CreatedByBaseEntity;
import com.jocoos.mybeautip.member.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

import static com.jocoos.mybeautip.domain.term.code.TermStatus.OPTIONAL;
import static java.util.stream.Collectors.toMap;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"term_id", "member_id"}))
@Entity
public class MemberTerm extends CreatedByBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private Boolean isAccept;

    private float version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id")
    private Term term;

    @Builder(builderClassName = "AutoMember")
    public MemberTerm(float version, Term term) {
        this.isAccept = true;
        this.version = version;
        this.term = term;
    }

    @Builder(builderClassName = "WithMember", builderMethodName = "builderWithMember")
    public MemberTerm(float version, Term term, Member member) {
        super.setMemberManually(member);
        this.isAccept = true;
        this.version = version;
        this.term = term;
    }

    public static Map<Long, Boolean> memberIdIsAgreeTermMap(List<MemberTerm> memberTerms) {
        return memberTerms.stream()
                .collect(toMap(MemberTerm::memberId, MemberTerm::getIsAccept));
    }

    private Long memberId() {
        return this.getMember().getId();
    }

    public boolean isTermDiff(long termId, float termVersion) {
        return termVersion != this.getVersion() || termId != this.getTerm().getId();
    }

    public void changeAcceptStatus(Boolean isAccept) {
        //TODO 예외정의 필요
        if (!OPTIONAL.equals(this.getTerm().getCurrentTermStatus()))
            throw new IllegalArgumentException();
        this.isAccept = isAccept;
    }

    public void updateVersion(float version) {
        this.isAccept = true;
        this.version = version;
    }
}
