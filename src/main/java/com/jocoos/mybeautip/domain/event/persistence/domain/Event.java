package com.jocoos.mybeautip.domain.event.persistence.domain;

import com.jocoos.mybeautip.audit.ModifiedDateAuditable;
import com.jocoos.mybeautip.domain.event.code.EventStatus;
import com.jocoos.mybeautip.domain.event.code.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "event")
public class Event extends ModifiedDateAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    public long id;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private EventType type;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private EventStatus status;

    @Column
    private Integer sorting;

    @Column
    private String title;

    @Column
    private String description;

    @Column
    private String imageUrl;

    @Column
    private int needPoint;

    @Column(nullable = false)
    private LocalDateTime startAt;

    @Column
    private LocalDateTime endAt;

    @OneToMany(mappedBy = "event")
    private List<EventProduct> eventProductList;

    @OneToMany(mappedBy = "event")
    private List<EventJoin> eventJoinList;

}
