package com.jocoos.mybeautip.member.detail;

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

    public void setInviterId(Long inviterId) {
        if (this.inviterId == null || this.inviterId < 1) {
            this.inviterId = inviterId;
        }
    }
}
