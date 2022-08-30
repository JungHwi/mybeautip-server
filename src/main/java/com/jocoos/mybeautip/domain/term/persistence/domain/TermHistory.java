package com.jocoos.mybeautip.domain.term.persistence.domain;

import com.jocoos.mybeautip.domain.term.code.TermStatus;
import com.jocoos.mybeautip.domain.term.code.TermUsedInType;
import com.jocoos.mybeautip.domain.term.persistence.converter.TermUsedInTypeListConverter;
import com.jocoos.mybeautip.global.code.HistoryType;
import com.jocoos.mybeautip.global.config.jpa.CreatedAtBaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class TermHistory extends CreatedAtBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "history_type")
    @Enumerated(EnumType.STRING)
    private HistoryType type;

    private long termId;

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

    @Builder
    public TermHistory(HistoryType type,
                       long termId,
                       String title,
                       String content,
                       TermStatus currentTermStatus,
                       List<TermUsedInType> usedInType,
                       float version,
                       TermStatus versionChangeStatus) {
        this.type = type;
        this.termId = termId;
        this.title = title;
        this.content = content;
        this.currentTermStatus = currentTermStatus;
        this.usedInType = usedInType;
        this.version = version;
        this.versionChangeStatus = versionChangeStatus;
    }
}

