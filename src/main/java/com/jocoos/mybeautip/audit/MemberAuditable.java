package com.jocoos.mybeautip.audit;

import javax.persistence.*;
import java.util.Date;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;

import com.jocoos.mybeautip.member.Member;

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
