package com.jocoos.mybeautip.domain.term.persistence.domain;

import com.jocoos.mybeautip.domain.term.code.TermStatus;
import com.jocoos.mybeautip.domain.term.code.TermType;
import com.jocoos.mybeautip.domain.term.code.TermUsedInType;
import com.jocoos.mybeautip.domain.term.persistence.converter.TermUsedInTypeListConverter;
import com.jocoos.mybeautip.global.config.jpa.BaseEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

/*
    약관 테이블
    currentTermStatus - 현재 약관의 타입
    usedInType - 약관 사용처, jpa method query 에서 in이 지정 단어라 usedInType 으로 정의
    versionChangeStatus - 약관 업데이트 내용의 타입
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "terms")
@Entity
public class Term extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 2021-07-28 현재 서버에서 약관을 관리하지 않기 때문에 임시로 넣은 칼럼, 추후 서버에서 약관 관리한다면 드롭할것
    @Enumerated(EnumType.STRING)
    private TermType type;

    private String title;

    private String content;

    @Enumerated(EnumType.STRING)
    private TermStatus currentTermStatus;

    @Column(name = "used_in")
    @Convert(converter = TermUsedInTypeListConverter.class)
    private List<TermUsedInType> usedInType;

    private float version;

    @Enumerated(EnumType.STRING)
    private TermStatus versionChangeStatus;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Term term = (Term) o;
        return id == term.id && Float.compare(term.version, version) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, version);
    }
}
