package com.jocoos.mybeautip.domain.notice.service.dao;

import com.jocoos.mybeautip.domain.notice.converter.NoticeConverter;
import com.jocoos.mybeautip.domain.notice.dto.EditNoticeRequest;
import com.jocoos.mybeautip.domain.notice.dto.SearchNoticeRequest;
import com.jocoos.mybeautip.domain.notice.dto.WriteNoticeRequest;
import com.jocoos.mybeautip.domain.notice.persistence.domain.Notice;
import com.jocoos.mybeautip.domain.notice.persistence.repository.NoticeRepository;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public Notice edit(EditNoticeRequest request) {
        Notice notice = this.get(request.getId());
        notice.editTitle(request.getTitle());
        notice.editDescription(request.getDescription());
        notice.editFiles(request.getFiles());

        return notice;
    }

    @Transactional
    public void delete(long noticeId) {
        Notice notice = this.get(noticeId);
        notice.delete();
    }

    @Transactional(readOnly = true)
    public Page<Notice> search(SearchNoticeRequest request) {
        return repository.search(request);
    }

    @Transactional(readOnly = true)
    public Notice get(long id) {
        return this.get(id, false);
    }

    @Transactional(readOnly = true)
    public Notice get(long id, boolean isView) {
        if (isView) {
            this.countViewCount(id);
        }

        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Notice not found. id - " + id));
    }

    @Transactional
    public void countViewCount(long id) {
        repository.viewCount(id);
    }

    @Transactional
    public Notice update(EditNoticeRequest request) {
        Notice notice = get(request.getId());
        return null;
    }
}
