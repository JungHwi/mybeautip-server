package com.jocoos.mybeautip.restapi.dto;

import com.jocoos.mybeautip.domain.term.code.TermType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
public class SignupRequest {
    @NotEmpty()
    private String socialId;
    private String grantType;
    private String username;
    private String email;
    private String avatarUrl;
    private String refreshToken;

    @NotNull
    private Set<TermType> termTypes;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignupRequest that = (SignupRequest) o;
        return socialId.equals(that.socialId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(socialId);
    }

    public void changeAvatarUrl(String uploadAvatarUrl) {
        this.avatarUrl = uploadAvatarUrl;
    }
}
