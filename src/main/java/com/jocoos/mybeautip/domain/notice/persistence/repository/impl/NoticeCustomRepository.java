package com.jocoos.mybeautip.domain.notice.persistence.repository.impl;

import com.jocoos.mybeautip.domain.notice.dto.SearchNoticeRequest;
import com.jocoos.mybeautip.domain.notice.persistence.domain.Notice;
import org.springframework.data.domain.Page;

public interface NoticeCustomRepository {

    Page<Notice> search(SearchNoticeRequest request);
}
