package com.jocoos.mybeautip.domain.member.persistence.repository;

import com.jocoos.mybeautip.domain.member.persistence.domain.UsernameCombinationWord;
import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsernameCombinationWordRepository extends DefaultJpaRepository<UsernameCombinationWord, Long> {

}
