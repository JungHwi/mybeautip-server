package com.jocoos.mybeautip.domain.term.persistence.domain;

import com.jocoos.mybeautip.global.config.jpa.CreatedByBaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MemberTerm extends CreatedByBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private boolean isAccept;

    private float version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id")
    private Term term;

    @Builder
    public MemberTerm(boolean isAccept, float version, Term term) {
        this.isAccept = isAccept;
        this.version = version;
        this.term = term;
    }
}
