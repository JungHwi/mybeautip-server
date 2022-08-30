package com.jocoos.mybeautip.member.block;

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
@Table(name = "member_blocks")
public class Block {
    @Column
    @CreatedDate
    public Date createdAt;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long me;
    @ManyToOne
    @JoinColumn(name = "you")
    private Member memberYou;

    @Enumerated(EnumType.STRING)
    private BlockStatus status;

    public Block(Long me, Member memberYou) {
        this.me = me;
        this.memberYou = memberYou;
    }

    public Long getYouId() {
        return memberYou.getId();
    }

    public void changeStatus(BlockStatus status) {
        this.status = status;
    }
}
