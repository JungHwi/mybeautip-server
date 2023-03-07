package com.jocoos.mybeautip.domain.broadcast.persistence.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class BroadcastCategory {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(insertable = false, updatable = false)
    private Long id;

    @Column
    private Long parentId;

    @Column
    private Integer sort;

    @Column
    private String title;

    @Column
    private String description;

    public BroadcastCategory(Long parentId, String title, String description) {
        this.parentId = parentId;
        this.title = title;
        this.description = description;
    }

    public boolean isGroup() {
        return parentId == null;
    }
}
