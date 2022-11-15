package com.jocoos.mybeautip.domain.member.persistence.domain;

import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.exception.ErrorCode;
import com.jocoos.mybeautip.member.code.SkinType;
import com.jocoos.mybeautip.member.code.SkinWorry;
import com.jocoos.mybeautip.member.code.converter.SkinWorrySetConverter;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, insertable = false, updatable = false)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(length = 20)
    private Long inviterId;

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private SkinType skinType;

    @Column(length = 50)
    @Convert(converter = SkinWorrySetConverter.class)
    private Set<SkinWorry> skinWorry;

    public void registerInviterId(Long inviterId) {
        if (this.inviterId == null || this.inviterId < 1 ) {
            this.inviterId = inviterId;
        } else if (this.inviterId.equals(inviterId)) {
            return;
        } else {
            throw new BadRequestException(ErrorCode.ALREADY_REGISTRATION, "친구 초대 코드가 이미 등록됨.");
        }
    }

    public MemberDetail(long memberId) {
        this.memberId = memberId;
    }

    public void setSkinWorry(Set<SkinWorry> skinWorrySet) {
        if (skinWorrySet.size() > 3) {
            throw new BadRequestException("SkinWorry size - " + skinWorrySet.size());
        }

        this.skinWorry = skinWorrySet;
    }
}
