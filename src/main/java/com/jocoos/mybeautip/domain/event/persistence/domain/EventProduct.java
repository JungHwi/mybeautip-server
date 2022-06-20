package com.jocoos.mybeautip.domain.event.persistence.domain;

import com.jocoos.mybeautip.domain.event.code.EventProductType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "event_product")
public class EventProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private long id;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column
    @Enumerated(EnumType.STRING)
    private EventProductType type;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int quantity;

    @Column(precision = 5, scale = 2, nullable = false)
    private BigDecimal percentage;

    @Column()
    private String imageUrl;

    @ManyToOne()
    @JoinColumn(name = "event_id", insertable = false, updatable = false)
    private Event event;
}
