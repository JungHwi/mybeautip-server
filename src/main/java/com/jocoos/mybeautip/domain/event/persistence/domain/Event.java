package com.jocoos.mybeautip.domain.event.persistence.domain;

import com.jocoos.mybeautip.audit.ModifiedDateAuditable;
import com.jocoos.mybeautip.domain.event.code.EventStatus;
import com.jocoos.mybeautip.domain.event.code.EventType;
import com.jocoos.mybeautip.global.code.UrlDirectory;
import com.jocoos.mybeautip.global.util.date.ZonedDateTimeUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.jocoos.mybeautip.global.util.ImageUrlConvertUtil.toUrl;

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
    private long id;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private EventType type;

    @Column
    private Long relationId;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private EventStatus status;

    @Formula("case status " +
            "when 'PROGRESS' then 0 " +
            "when 'END' then 1 " +
            "else 2 end")
    private int statusSorting;

    @Column
    private Integer sorting;

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

    @Column
    private String color;

    @Column(nullable = false)
    private ZonedDateTime startAt;

    @Column
    private ZonedDateTime endAt;

    @OneToMany(mappedBy = "event")
    private List<EventProduct> eventProductList;

    @OneToMany(mappedBy = "event")
    private List<EventJoin> eventJoinList;

    public String getBannerImageUrl() {
        return toEventUrl(bannerImageFile);
    }

    public String getDetailImageUrl() {
        return toEventUrl(imageFile);
    }

    public String getThumbnailImageUrl() {
        return toEventUrl(thumbnailImageFile);
    }

    public String getShareSquareImageUrl() {
        return toEventUrl(shareSquareImageFile);
    }

    public String getShareRectangleImageUrl() {
        return toEventUrl(shareRectangleImageFile);
    }

    public ZonedDateTime getZonedCreatedAt() {
        return ZonedDateTimeUtil.toUTCZoned(createdAt);
    }

    public static List<Long> getIds(List<Event> events) {
        return events.stream().map(Event::getId).collect(Collectors.toList());
    }

    private String toEventUrl(String filename) {
        return toUrl(filename, UrlDirectory.EVENT);
    }
}
