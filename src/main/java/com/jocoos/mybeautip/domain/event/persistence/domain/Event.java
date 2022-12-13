package com.jocoos.mybeautip.domain.event.persistence.domain;

import com.jocoos.mybeautip.audit.ModifiedDateAuditable;
import com.jocoos.mybeautip.domain.event.code.EventStatus;
import com.jocoos.mybeautip.domain.event.code.EventType;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;

import static com.jocoos.mybeautip.domain.event.code.EventStatus.END;
import static com.jocoos.mybeautip.domain.event.code.EventStatus.PROGRESS;
import static com.jocoos.mybeautip.global.exception.ErrorCode.NOT_SUPPORTED_FIX_STATUS;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "event")
public class Event extends ModifiedDateAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private Long id;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private EventType type;

    @Column
    private Long relationId;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private EventStatus status;

    @Column(nullable = false)
    private Boolean isVisible;

    @Formula("case status " +
            "when 'PROGRESS' then 0 " +
            "when 'END' then 1 " +
            "else 2 end")
    private int statusSorting;

    @Column
    private String title;

    @Column
    private String description;

    @Column
    private String imageFile;

    @Column
    private String thumbnailImageFile;

    @Column
    private String bannerImageFile;

    @Column
    private String shareSquareImageFile;

    @Column
    private String shareRectangleImageFile;

    @Column
    private int needPoint;

    @Column(nullable = false)
    private ZonedDateTime startAt;

    @Column
    private ZonedDateTime endAt;

    @Column
    private ZonedDateTime reservationAt;

    @Embedded
    private FixSorting fixSorting;

    @OneToMany(mappedBy = "event", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<EventProduct> eventProductList;

    @OneToMany(mappedBy = "event")
    private List<EventJoin> eventJoinList;

    @PostPersist
    public void postPersist() {
        if (CollectionUtils.isNotEmpty(eventProductList)) {
            eventProductList.forEach(product -> {
                if (product != null) {
                    product.setEvent(this);
                }
            });
        }
    }

    public void fix(Integer lastSortOrder) {
        if (!status.equals(PROGRESS)) {
            throw new BadRequestException(NOT_SUPPORTED_FIX_STATUS, "need status : " + PROGRESS + " request status : " + status);
        }
        fixSorting = FixSorting.fix(lastSortOrder);
    }

    public void unFix() {
        fixSorting = FixSorting.unFix();
    }

    public void unFixByStatusChange(EventStatus status) {
        if (END.equals(status)) {
            unFix();
        }
    }
}
