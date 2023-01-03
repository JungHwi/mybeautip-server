package com.jocoos.mybeautip.domain.notice.service;

import com.jocoos.mybeautip.client.aws.s3.AwsS3Handler;
import com.jocoos.mybeautip.domain.notice.converter.NoticeConverter;
import com.jocoos.mybeautip.domain.notice.dto.*;
import com.jocoos.mybeautip.domain.notice.persistence.domain.Notice;
import com.jocoos.mybeautip.domain.notice.service.dao.NoticeDao;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.jocoos.mybeautip.global.code.UrlDirectory.NOTICE;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeDao dao;
    private final NoticeConverter converter;
    private final AwsS3Handler awsS3Handler;


    @Transactional
    public NoticeResponse write(WriteNoticeRequest request) {
        Notice notice = dao.write(request);
        awsS3Handler.copy(request.getFiles(), NOTICE.getDirectory());

        return converter.converts(notice);
    }

    @Transactional
    public NoticeResponse edit(EditNoticeRequest request) {
        Notice notice = dao.edit(request);
        awsS3Handler.editFiles(request.getFiles(), NOTICE.getDirectory());

        return converter.converts(notice);
    }

    @Transactional
    public void delete(long noticeId) {
        dao.delete(noticeId);
    }

    @Transactional(readOnly = true)
    public Page<NoticeListResponse> search(SearchNoticeRequest request) {
        Page<Notice> noticePage = dao.search(request);
        return converter.convertsToListResponsePage(noticePage);
    }

    @Transactional(readOnly = true)
    public NoticeResponse get(long noticeId) {
        return this.get(noticeId, false);
    }

    @Transactional
    public NoticeResponse get(long noticeId, boolean isView) {
        Notice notice = dao.get(noticeId, isView);
        return converter.converts(notice);
    }


}
