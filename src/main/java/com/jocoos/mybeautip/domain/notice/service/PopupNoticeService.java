package com.jocoos.mybeautip.domain.notice.service;

import com.jocoos.mybeautip.domain.notice.converter.PopupNoticeConverter;
import com.jocoos.mybeautip.domain.notice.dto.PopupNoticeResponse;
import com.jocoos.mybeautip.domain.notice.persistence.domain.PopupNotice;
import com.jocoos.mybeautip.domain.notice.service.dao.PopupNoticeDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PopupNoticeService {

    private final PopupNoticeDao popupNoticeDao;
    private final PopupNoticeConverter converter;

    @Transactional(readOnly = true)
    public List<PopupNoticeResponse> getNotices() {
        List<PopupNotice> notices = popupNoticeDao.getAllActive();
        return converter.convert(notices);
    }
}
