package com.jocoos.mybeautip.domain.event.persistence.domain;

import com.jocoos.mybeautip.domain.event.code.EventProductType;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.exception.ErrorCode;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "event_product")
public class EventProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private Long id;

    @Column
    @Enumerated(EnumType.STRING)
    private EventProductType type;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int quantity;

    @Column()
    private int price;

    @Column()
    private String imageFile;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    public EventProduct winPrize() {
        if (quantity <= 0) {
            throw new BadRequestException(ErrorCode.SOLD_OUT, this.name + " product sold out.");
        }

        this.quantity--;

        return this;
    }
}
