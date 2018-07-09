package com.jocoos.mybeautip.audit;

import java.util.Date;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable<U> {

  @CreatedBy
  protected U createdBy;

  @CreatedDate
  protected Date createdAt;

  @LastModifiedBy
  protected U modifiedBy;

  @LastModifiedDate
  protected Date modifiedAt;

}
