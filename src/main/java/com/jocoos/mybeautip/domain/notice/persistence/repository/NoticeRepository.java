package com.jocoos.mybeautip.domain.notice.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.notice.persistence.domain.Notice;
import com.jocoos.mybeautip.domain.notice.persistence.repository.impl.NoticeCustomRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends ExtendedQuerydslJpaRepository<Notice, Long>, NoticeCustomRepository {

    @Modifying
    @Query("UPDATE Notice notice SET notice.viewCount = notice.viewCount + 1 WHERE notice.id = :noticeId")
    void viewCount(@Param("noticeId") long noticeId);

}
