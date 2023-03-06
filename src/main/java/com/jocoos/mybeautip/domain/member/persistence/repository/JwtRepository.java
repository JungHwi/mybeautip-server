package com.jocoos.mybeautip.domain.member.persistence.repository;

import com.jocoos.mybeautip.domain.member.persistence.domain.Jwt;
import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.ZonedDateTime;
import java.util.Optional;

public interface JwtRepository extends DefaultJpaRepository<Jwt, String> {

    Optional<Jwt> findByIdAndExpiryAtGreaterThan(String id, ZonedDateTime now);

    @Modifying
    @Query("DELETE FROM Jwt jwt WHERE jwt.expiryAt < now()")
    int deleteExpiredJwt();
}
