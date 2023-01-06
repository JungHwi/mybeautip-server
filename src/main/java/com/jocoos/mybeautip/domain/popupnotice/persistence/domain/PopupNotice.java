package com.jocoos.mybeautip.domain.popupnotice.persistence.domain;

import com.jocoos.mybeautip.audit.CreatedDateAuditable;
import com.jocoos.mybeautip.domain.popupnotice.code.PopupNoticeLinkType;
import com.jocoos.mybeautip.domain.popupnotice.code.PopupNoticeStatus;
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
    private PopupNoticeStatus status;

    @Column
    private String filename;

    @Column
    @Enumerated(EnumType.STRING)
    private PopupNoticeLinkType linkType;

    @Column
    private String linkArgument;

    @Column
    private ZonedDateTime startedAt;

    @Column
    private ZonedDateTime endedAt;

}
