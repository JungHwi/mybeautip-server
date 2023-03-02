package com.jocoos.mybeautip.domain.broadcast.persistence.domain;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastCategoryType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.EnumType.STRING;
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

    @Enumerated(STRING)
    @Column
    private BroadcastCategoryType type;

    @Column
    private Integer sort;

    @Column
    private String title;

    @Column
    private String description;

    public BroadcastCategory(Long parentId, BroadcastCategoryType type, String title, String description) {
        this.parentId = parentId;
        this.type = type;
        this.title = title;
        this.description = description;
    }

    public boolean isType(BroadcastCategoryType type) {
        return this.type == type;
    }
}
