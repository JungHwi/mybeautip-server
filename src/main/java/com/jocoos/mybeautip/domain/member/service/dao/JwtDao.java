package com.jocoos.mybeautip.domain.member.service.dao;

import com.jocoos.mybeautip.domain.member.persistence.domain.Jwt;
import com.jocoos.mybeautip.domain.member.persistence.repository.JwtRepository;
import com.jocoos.mybeautip.global.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;


@Service
@RequiredArgsConstructor
public class JwtDao {

    private final JwtRepository repository;

    @Transactional
    public Jwt registerRefreshToken(String username, String refreshToken, int validitySeconds) {
        Jwt jwt = new Jwt(username, refreshToken, ZonedDateTime.now().plusSeconds(validitySeconds));
        return repository.save(jwt);
    }

    @Transactional(readOnly = true)
    public Jwt get(String username) {
        return repository.findById(username)
                .orElseThrow(MemberNotFoundException::new);
    }

    @Transactional
    public int deleteExpiredJwt() {
        return repository.deleteExpiredJwt();
    }
}
