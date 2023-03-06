package com.jocoos.mybeautip.domain.member.persistence.repository;

import com.jocoos.mybeautip.domain.member.persistence.domain.Jwt;
import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository;

import java.time.ZonedDateTime;
import java.util.Optional;

public interface JwtRepository extends DefaultJpaRepository<Jwt, String> {

    Optional<Jwt> findByIdAndExpiryAtGreaterThan(String id, ZonedDateTime now);

    int deleteByExpiryAtLessThan(ZonedDateTime now);
}
