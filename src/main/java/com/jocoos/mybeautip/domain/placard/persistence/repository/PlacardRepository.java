package com.jocoos.mybeautip.domain.placard.persistence.repository;

import com.jocoos.mybeautip.domain.placard.code.PlacardStatus;
import com.jocoos.mybeautip.domain.placard.code.PlacardTabType;
import com.jocoos.mybeautip.domain.placard.persistence.domain.Placard;
import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlacardRepository extends DefaultJpaRepository<Placard, Long> {

    @Query("SELECT placard " +
            " FROM Placard AS placard " +
            "   INNER JOIN FETCH PlacardDetail AS placardDetail ON placard.id = placardDetail.placard.id " +
            " WHERE placardDetail.tabType = :tabType " +
            "   AND placard.status = :placardStatus " +
            "   AND placard.startedAt < CURRENT_TIMESTAMP " +
            "   AND placard.endedAt > CURRENT_TIMESTAMP " +
            " ORDER BY placard.id DESC")
    List<Placard> findByActivePlacard(PlacardStatus placardStatus, PlacardTabType tabType);
}