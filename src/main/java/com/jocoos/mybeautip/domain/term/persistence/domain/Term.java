package com.jocoos.mybeautip.domain.term.persistence.domain;

import com.jocoos.mybeautip.domain.term.code.TermStatus;
import com.jocoos.mybeautip.domain.term.code.TermUsedInType;
import com.jocoos.mybeautip.global.config.jpa.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/*
    약관 테이블
    isRequired - 필수 여부
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "terms")
@Entity
public class Term extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;

    private String content;

    // 현재 약관의 타입
    @Enumerated(EnumType.STRING)
    private TermStatus currentTermStatus;

    @Enumerated(EnumType.STRING)
    private TermUsedInType usedInType;

    private float version;

    // 약관 업데이트 내용의 타입
    @Enumerated(EnumType.STRING)
    private TermStatus versionChangeStatus;
}
