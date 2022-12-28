package com.jocoos.mybeautip.domain.notice.service;

import com.jocoos.mybeautip.client.aws.s3.AwsS3Handler;
import com.jocoos.mybeautip.domain.notice.converter.NoticeConverter;
import com.jocoos.mybeautip.domain.notice.dto.NoticeResponse;
import com.jocoos.mybeautip.domain.notice.dto.WriteNoticeRequest;
import com.jocoos.mybeautip.domain.notice.persistence.domain.Notice;
import com.jocoos.mybeautip.domain.notice.service.dao.NoticeDao;
import lombok.RequiredArgsConstructor;
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

}
