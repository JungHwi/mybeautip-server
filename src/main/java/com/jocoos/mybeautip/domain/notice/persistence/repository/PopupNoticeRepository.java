package com.jocoos.mybeautip.domain.notice.persistence.repository;

import com.jocoos.mybeautip.domain.notice.code.NoticeStatus;
import com.jocoos.mybeautip.domain.notice.persistence.domain.PopupNotice;
import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository;

import java.time.ZonedDateTime;
import java.util.List;

public interface PopupNoticeRepository extends DefaultJpaRepository<PopupNotice, Long> {

    List<PopupNotice> findAllByStatusAndEndedAtAfter(NoticeStatus status, ZonedDateTime dateTime);

}
