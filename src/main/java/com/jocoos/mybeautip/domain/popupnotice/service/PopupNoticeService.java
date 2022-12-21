package com.jocoos.mybeautip.domain.popupnotice.service;

import com.jocoos.mybeautip.domain.popupnotice.converter.PopupNoticeConverter;
import com.jocoos.mybeautip.domain.popupnotice.dto.PopupNoticeResponse;
import com.jocoos.mybeautip.domain.popupnotice.persistence.domain.PopupNotice;
import com.jocoos.mybeautip.domain.popupnotice.service.dao.PopupNoticeDao;
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
