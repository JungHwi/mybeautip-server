package com.jocoos.mybeautip.domain.notice.service.dao;

import com.jocoos.mybeautip.domain.notice.converter.NoticeConverter;
import com.jocoos.mybeautip.domain.notice.dto.EditNoticeRequest;
import com.jocoos.mybeautip.domain.notice.dto.SearchNoticeRequest;
import com.jocoos.mybeautip.domain.notice.dto.WriteNoticeRequest;
import com.jocoos.mybeautip.domain.notice.persistence.domain.Notice;
import com.jocoos.mybeautip.domain.notice.persistence.repository.NoticeRepository;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeDao {

    private final NoticeRepository repository;
    private final NoticeConverter converter;

    @Transactional
    public Notice write(WriteNoticeRequest request) {
        Notice notice = converter.converts(request);
        return repository.save(notice);
    }

    @Transactional(readOnly = true)
    public List<Notice> search(SearchNoticeRequest request) {
        return repository.search(request);
    }

    @Transactional(readOnly = true)
    public Notice get(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Notice not found. id - " + id));
    }

    @Transactional
    public Notice update(EditNoticeRequest request) {
        Notice notice = get(request.getId());
        return null;
    }
}
