package com.jocoos.mybeautip.domain.member.persistence.domain;

import com.jocoos.mybeautip.global.exception.AuthenticationException;
import com.jocoos.mybeautip.global.exception.ErrorCode;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.ZonedDateTime;

@Entity
@NoArgsConstructor
public class Jwt {
    @Id
    private String id;

    @Column
    private String refreshToken;

    @Column
    private ZonedDateTime expiryAt;

    @Builder
    public Jwt(String id, String refreshToken, ZonedDateTime expiryAt) {
        this.id = id;
        this.refreshToken = refreshToken;
        this.expiryAt = expiryAt;
    }

    public boolean valid(String refreshToken) {
        if (this.refreshToken.equals(refreshToken) && expiryAt.isAfter(ZonedDateTime.now())) {
            return true;
        } else {
            throw new AuthenticationException(ErrorCode.INVALID_TOKEN);
        }
    }
}
