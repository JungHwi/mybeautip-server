package com.jocoos.mybeautip.domain.notice.persistence.repository.impl;

import com.jocoos.mybeautip.domain.notice.dto.SearchNoticeRequest;
import com.jocoos.mybeautip.domain.notice.persistence.domain.Notice;

import java.util.List;

public class NoticeCustomRepositoryImpl implements NoticeCustomRepository {
    @Override
    public List<Notice> search(SearchNoticeRequest request) {
        return null;
    }
}
