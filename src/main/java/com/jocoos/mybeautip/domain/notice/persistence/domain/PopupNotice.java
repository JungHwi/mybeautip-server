package com.jocoos.mybeautip.domain.notice.persistence.domain;

import com.jocoos.mybeautip.audit.CreatedDateAuditable;
import com.jocoos.mybeautip.domain.notice.code.NoticeLinkType;
import com.jocoos.mybeautip.domain.notice.code.NoticeStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class PopupNotice extends CreatedDateAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    @Enumerated(EnumType.STRING)
    private NoticeStatus status;

    @Column
    private String filename;

    @Column
    @Enumerated(EnumType.STRING)
    private NoticeLinkType linkType;

    @Column
    private String linkArgument;

    @Column
    private ZonedDateTime startedAt;

    @Column
    private ZonedDateTime endedAt;

}
