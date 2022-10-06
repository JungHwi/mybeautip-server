package com.jocoos.mybeautip.domain.scrap.persistence.domain;

import com.jocoos.mybeautip.domain.scrap.code.ScrapType;
import com.jocoos.mybeautip.global.config.jpa.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "scrap")
public class Scrap extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private Long id;

    @Column
    private long memberId;

    @Enumerated(EnumType.STRING)
    private ScrapType type;

    @Column
    private long relationId;

    @Column
    private Boolean isScrap;

    public Scrap(long memberId, ScrapType type, long relationId) {
        this.memberId = memberId;
        this.type = type;
        this.relationId = relationId;
        this.isScrap = false;
    }
}
