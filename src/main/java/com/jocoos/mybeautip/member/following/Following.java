package com.jocoos.mybeautip.member.following;

import com.jocoos.mybeautip.member.Member;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "member_followings")
public class Following {
    @Column
    @CreatedDate
    public Date createdAt;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "me")
    private Member memberMe;
    @ManyToOne
    @JoinColumn(name = "you")
    private Member memberYou;

    public Following(Member memberMe, Member memberYou) {
        this.memberMe = memberMe;
        this.memberYou = memberYou;
    }
}