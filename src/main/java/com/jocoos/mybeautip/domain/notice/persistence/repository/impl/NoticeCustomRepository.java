package com.jocoos.mybeautip.domain.notice.persistence.repository.impl;

import com.jocoos.mybeautip.domain.notice.dto.SearchNoticeRequest;
import com.jocoos.mybeautip.domain.notice.persistence.domain.Notice;

import java.util.List;

public interface NoticeCustomRepository {

    List<Notice> search(SearchNoticeRequest request);
}
