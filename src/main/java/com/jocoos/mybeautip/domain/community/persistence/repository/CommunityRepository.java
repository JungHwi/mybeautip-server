package com.jocoos.mybeautip.domain.community.persistence.repository;

import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityRepository extends DefaultJpaRepository<Community, Long> {


}
