package com.jocoos.mybeautip.admin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jocoos.mybeautip.legacy.store.LegacyStore;
import com.jocoos.mybeautip.member.Member;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "admin_members")
public class AdminMember {

    @Id
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private LegacyStore legacyStore;

    @Column(nullable = false)
    @CreatedDate
    private Date createdAt;
}
