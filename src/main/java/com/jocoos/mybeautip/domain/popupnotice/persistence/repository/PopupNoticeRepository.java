package com.jocoos.mybeautip.domain.popupnotice.persistence.repository;

import com.jocoos.mybeautip.domain.popupnotice.code.NoticeStatus;
import com.jocoos.mybeautip.domain.popupnotice.persistence.domain.PopupNotice;
import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository;

import java.time.ZonedDateTime;
import java.util.List;

public interface PopupNoticeRepository extends DefaultJpaRepository<PopupNotice, Long> {

    List<PopupNotice> findAllByStatusAndEndedAtAfter(NoticeStatus status, ZonedDateTime dateTime);

}
