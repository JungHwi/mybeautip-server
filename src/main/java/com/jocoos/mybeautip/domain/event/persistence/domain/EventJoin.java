package com.jocoos.mybeautip.domain.event.persistence.domain;

import com.jocoos.mybeautip.audit.CreatedDateAuditable;
import com.jocoos.mybeautip.domain.event.code.EventJoinStatus;
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
@Table(name = "event_join")
public class EventJoin extends CreatedDateAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EventJoinStatus status;

    @Column(name = "event_id")
    private long eventId;

    @Column(name = "event_product_id")
    private Long eventProductId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "event_id", insertable = false, updatable = false)
    private Event event;

    @Column(nullable = false)
    private long memberId;

    @Column
    private String recipientInfo;

    @ManyToOne()
    @JoinColumn(name = "event_product_id", insertable = false, updatable = false)
    private EventProduct eventProduct;

}
