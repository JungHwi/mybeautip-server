package com.jocoos.mybeautip.domain.popup.persistence.repository;

import com.jocoos.mybeautip.domain.popup.persistence.domain.Popup;
import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PopupRepository extends DefaultJpaRepository<Popup, Long> {


    @Query("SELECT popup " +
            "FROM Popup AS popup " +
            "WHERE popup.status = 'ACTIVE' " +
            "   AND popup.startedAt < CURRENT_TIMESTAMP " +
            "   AND popup.endedAt > CURRENT_TIMESTAMP " +
            "ORDER BY popup.id DESC")
    List<Popup> findByActivePopup();
}
