package com.jocoos.mybeautip.domain.notice.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.notice.persistence.domain.Notice;
import com.jocoos.mybeautip.domain.notice.persistence.repository.impl.NoticeCustomRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends ExtendedQuerydslJpaRepository<Notice, Long>, NoticeCustomRepository {
}
