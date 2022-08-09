package com.jocoos.mybeautip.domain.community.persistence.domain;

import com.jocoos.mybeautip.global.config.jpa.ModifiedAtBaseEntity;
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
@Table(name = "community_report")
public class CommunityReport extends ModifiedAtBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private Long id;

    @Column
    private long memberId;

    @Column
    private long communityId;

    @Column
    private boolean isReport;
}