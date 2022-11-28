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

    public void editTitle(String title) {
        this.title = title;
    }

    public void editStatus(PlacardStatus status) {
        this.status = status;
    }

    public void editLinkType(PlacardLinkType linkType) {
        this.linkType = linkType;
    }

    public void editLinkArgument(String linkArgument) {
        this.linkArgument = linkArgument;
    }

    public void editDescription(String description) {
        this.description = description;
    }

    public void editColor(String color) {
        this.color = color;
    }

    public void editStartedAt(ZonedDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public void editEndedAt(ZonedDateTime endedAt) {
        this.endedAt = endedAt;
    }

    public void editImageUrl(String imageUrl) {
        detailList.stream()
                .findFirst()
                .ifPresent(detail -> detail.replaceFile(imageUrl));
    }

    public void changeStatus(boolean isActive) {
        this.status = getStatus(isActive);
    }

    private PlacardStatus getStatus(boolean isActive) {
        return isActive ? ACTIVE : INACTIVE;
    }

    public void topFix(boolean isTopFix) {
        if (isTopFix) {
            validActive();
        }
        this.isTopFix = isTopFix;
    }

    private void validActive() {
        if (status.equals(INACTIVE)) {
            throw new BadRequestException(ONLY_ACTIVE_CAN_FIX);
        }
    }
}
