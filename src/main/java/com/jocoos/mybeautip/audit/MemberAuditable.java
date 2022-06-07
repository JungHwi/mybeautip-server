package com.jocoos.mybeautip.audit;

import com.jocoos.mybeautip.member.Member;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class MemberAuditable {

    @ManyToOne
    @JoinColumn(name = "created_by")
    @CreatedBy
    protected Member createdBy;

    @Column
    @CreatedDate
    protected Date createdAt;
}
