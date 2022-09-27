package com.jocoos.mybeautip.domain.placard.persistence.domain;

import com.jocoos.mybeautip.audit.CreatedDateAuditable;
import com.jocoos.mybeautip.domain.placard.code.PlacardLinkType;
import com.jocoos.mybeautip.domain.placard.code.PlacardStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "placard")
public class Placard extends CreatedDateAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Enumerated(EnumType.STRING)
    private PlacardStatus status;

    @Column
    @Enumerated(EnumType.STRING)
    private PlacardLinkType linkType;

    @Column
    private String linkArgument;

    @Column
    private String description;

    @Column
    private String color;

    @Column(nullable = false, columnDefinition="TIMESTAMP")
    private ZonedDateTime startedAt;

    @Column(nullable = false, columnDefinition="TIMESTAMP")
    private ZonedDateTime endedAt;

    @OneToMany(mappedBy = "placard")
    private List<PlacardDetail> detailList;

}
