package com.jocoos.mybeautip.domain.community.persistence.domain;

import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "community_category")
public class CommunityCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    public Long id;

    @Column
    public Long parentId;

    @Enumerated(EnumType.STRING)
    private CommunityCategoryType type;

    @Column
    private Integer sort;

    @Column
    private String title;

    @Column
    private String description;

    @Column
    private String hint;
}
