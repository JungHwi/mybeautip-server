package com.jocoos.mybeautip.domain.placard.persistence.domain;

import com.jocoos.mybeautip.audit.CreatedDateAuditable;
import com.jocoos.mybeautip.domain.placard.code.PlacardLinkType;
import com.jocoos.mybeautip.domain.placard.code.PlacardStatus;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.jocoos.mybeautip.domain.placard.code.PlacardStatus.ACTIVE;
import static com.jocoos.mybeautip.domain.placard.code.PlacardStatus.INACTIVE;
import static com.jocoos.mybeautip.global.exception.ErrorCode.ONLY_ACTIVE_CAN_FIX;
import static com.jocoos.mybeautip.global.util.date.ZonedDateTimeUtil.toUTCZoned;
import static java.lang.Boolean.TRUE;

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
    private String title;

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

    @Column
    private Integer sorting;

    // 상단 고정만 순서 변경 가능
    @Column
    private Boolean isTopFix;

    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    private ZonedDateTime startedAt;

    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    private ZonedDateTime endedAt;

    @OneToMany(mappedBy = "placard", cascade = CascadeType.ALL)
    private List<PlacardDetail> detailList = new ArrayList<>();

    public void initDetail(PlacardDetail detail) {
        this.detailList = Collections.singletonList(detail);
        detail.setPlacard(this);
    }

    public String getImageUrl() {
        return detailList.stream()
                .findFirst()
                .map(PlacardDetail::getImageUrl)
                .orElse(null);
    }

    public ZonedDateTime getCreatedAtZoned() {
        return toUTCZoned(createdAt);
    }

    public void changeStatus(boolean isActive) {
        this.status = getStatus(isActive);
    }

    private PlacardStatus getStatus(boolean isActive) {
        return isActive ? ACTIVE : INACTIVE;
    }

    public void validActive() {
        if (status.equals(INACTIVE)) {
            throw new BadRequestException(ONLY_ACTIVE_CAN_FIX);
        }
    }

    @Builder(builderClassName = "PlacardEditBuilder", builderMethodName = "editBuilder")
    public Placard(Placard fromOriginal,
                   String title,
                   PlacardStatus status,
                   PlacardLinkType linkType,
                   String linkArgument,
                   String description,
                   String color,
                   ZonedDateTime startedAt,
                   ZonedDateTime endedAt,
                   String imageUrl) {
        this.id = fromOriginal.id;
        this.createdAt = fromOriginal.createdAt;
        this.sorting = fromOriginal.sorting;
        this.isTopFix = fromOriginal.isTopFix;
        this.title = title;
        this.status = status;
        this.linkType = linkType;
        this.linkArgument = linkArgument;
        this.description = description;
        this.color = color;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        fromOriginal.editImageUrl(imageUrl);
    }

    private void editImageUrl(String imageUrl) {
        detailList.stream()
                .findFirst()
                .ifPresent(detail -> detail.replaceFile(imageUrl));
    }

    public Boolean isTopFixTrueOrNull() {
        return TRUE.equals(isTopFix) ? true : null;
    }
}
