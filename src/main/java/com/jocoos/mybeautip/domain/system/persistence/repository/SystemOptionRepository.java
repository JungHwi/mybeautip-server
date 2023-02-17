package com.jocoos.mybeautip.domain.system.persistence.repository;

import com.jocoos.mybeautip.domain.system.code.SystemOptionType;
import com.jocoos.mybeautip.domain.system.persistence.domain.SystemOption;
import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemOptionRepository extends DefaultJpaRepository<SystemOption, SystemOptionType> {
}
