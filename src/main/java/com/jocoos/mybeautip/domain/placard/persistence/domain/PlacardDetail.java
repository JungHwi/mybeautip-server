package com.jocoos.mybeautip.domain.placard.persistence.domain;

import com.jocoos.mybeautip.domain.placard.code.PlacardTabType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "placard_detail")
public class PlacardDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    @Enumerated(EnumType.STRING)
    private PlacardTabType tabType;

    @ManyToOne(optional = false)
    @JoinColumn(name = "placard_id", insertable = false, updatable = false)
    Placard placard;

    @Column
    String imageUrl;
}
