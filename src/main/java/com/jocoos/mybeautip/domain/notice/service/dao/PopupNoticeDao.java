package com.jocoos.mybeautip.domain.notice.service.dao;

import com.jocoos.mybeautip.domain.notice.persistence.domain.PopupNotice;
import com.jocoos.mybeautip.domain.notice.persistence.repository.PopupNoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

import static com.jocoos.mybeautip.domain.notice.code.NoticeStatus.ACTIVE;

@RequiredArgsConstructor
@Service
public class PopupNoticeDao {

    private final PopupNoticeRepository repository;

    @Transactional(readOnly = true)
    public List<PopupNotice> getAllActive() {
        return repository.findAllByStatusAndEndedAtAfter(ACTIVE, ZonedDateTime.now());
    }
}
