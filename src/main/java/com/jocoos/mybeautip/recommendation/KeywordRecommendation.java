package com.jocoos.mybeautip.recommendation;

import com.jocoos.mybeautip.audit.MemberAuditable;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.tag.Tag;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "recommended_keywords")
public class KeywordRecommendation extends MemberAuditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 1: member, 2: tag
     */
    @Column(nullable = false)
    private int category;

    @ManyToOne
    @JoinColumn(name = "member")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "tag")
    private Tag tag;

    @Column(nullable = false)
    private int seq;

    @Column
    @LastModifiedDate
    private Date modifiedAt;

    @Column
    private Date startedAt;

    @Column
    private Date endedAt;

    public KeywordRecommendation(Member member, int seq) {
        this.category = 1;
        this.member = member;
        this.seq = seq;
    }

    public KeywordRecommendation(Tag tag, int seq) {
        this.category = 2;
        this.tag = tag;
        this.seq = seq;
    }
}