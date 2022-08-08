package com.jocoos.mybeautip.global.config.jpa;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import java.time.ZonedDateTime;

@Getter
@MappedSuperclass
public class ModifiedAtBaseEntity {
    @Column(updatable = false, nullable = false)
    private ZonedDateTime createdAt;

    @Column(updatable = false, nullable = false)
    private ZonedDateTime modifiedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = ZonedDateTime.now();
        this.modifiedAt = ZonedDateTime.now();
    }
}
