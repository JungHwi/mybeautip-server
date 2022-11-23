package com.jocoos.mybeautip.domain.community.persistence.domain;

import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import com.jocoos.mybeautip.domain.member.code.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

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

    @Column
    private Boolean isInSummary;

    public boolean isCategoryType(CommunityCategoryType type) {
        return Objects.equals(type, this.type);
    }

    public void validWriteAuth(Role role) {
        type.validWriteAuth(role);
    }

    public void validReadAuth(Role role) {
        type.validReadAuth(role);
    }
}
