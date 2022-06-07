package com.jocoos.mybeautip.audit;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class ModifiedDateAuditable {

    @Column(nullable = false)
    @CreatedDate
    protected Date createdAt;

    @Column
    @LastModifiedDate
    protected Date modifiedAt;

}
