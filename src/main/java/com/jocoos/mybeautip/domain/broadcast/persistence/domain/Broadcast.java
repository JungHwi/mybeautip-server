package com.jocoos.mybeautip.domain.broadcast.persistence.domain;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastEditCommand;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastViewerList;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastViewerVo;
import com.jocoos.mybeautip.global.config.jpa.CreatedAtBaseEntity;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.persistence.*;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus.*;
import static com.jocoos.mybeautip.global.code.UrlDirectory.BROADCAST;
import static com.jocoos.mybeautip.global.util.FileUtil.getFileName;
import static com.jocoos.mybeautip.global.util.ImageUrlConvertUtil.toUrl;
import static com.jocoos.mybeautip.global.validator.ObjectValidator.requireNonNull;
import static com.jocoos.mybeautip.global.validator.StringValidator.validateMaxLengthWithoutWhiteSpace;
import static java.time.ZonedDateTime.now;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@ParametersAreNonnullByDefault
@Entity
public class Broadcast extends CreatedAtBaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(unique = true)
    private Long videoKey;

    // used for chat
    @Column
    private String chatChannelKey;

    @Enumerated(STRING)
    private BroadcastStatus status;

    // generated column
    @Column(insertable = false, updatable = false)
    private int sortedStatus;

    @Column(nullable = false)
    private Long memberId;

    @Column
    private String title;

    @Column
    private String url;

    @Column
    private String thumbnail;

    @Column
    private String notice;

    @Column
    private Boolean canChat;

    @Column
    private Boolean isSoundOn;

    @Column
    private Boolean isScreenShow;

    @Column
    private ZonedDateTime pausedAt;

    @Column
    private ZonedDateTime startedAt;

    @Column
    private ZonedDateTime endedAt;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "category_id")
    private BroadcastCategory category;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "id")
    private BroadcastStatistics statistics;

    @Embedded
    public BroadcastViewerList viewerList = new BroadcastViewerList();

    @Builder
    public Broadcast(Long memberId,
                     String title,
                     String thumbnailUrl,
                     ZonedDateTime startedAt,
                     BroadcastCategory category,
                     @Nullable String notice,
                     boolean isStartNow) {
        requireNonNull(memberId, "memberId");
        this.memberId = memberId;
        this.canChat = true;
        this.isSoundOn = true;
        this.isScreenShow = true;
        initStatusAndStartedAt(isStartNow, startedAt);
        setCategory(category);
        setTitle(title);
        setThumbnail(thumbnailUrl);
        setNotice(notice);
    }

    @PostPersist
    public void postPersist() {
        if (statistics == null) {
            statistics = new BroadcastStatistics(this.id);
        }
    }

    public List<Long> syncViewer(List<BroadcastViewerVo> newViewers) {
        requireNonNull(newViewers, "newViewers");
        return this.viewerList.outSync(newViewers);
    }

    // video and channel key will get from external service so updated later
    public void updateVideoAndChannelKey(Long videoKey, String chatChannelKey) {
        requireNonNull(videoKey, "videoKey");
        requireNonNull(chatChannelKey, "chatChannelKey");
        this.videoKey = videoKey;
        this.chatChannelKey = chatChannelKey;
    }

    public void edit(BroadcastEditCommand command) {
        requireNonNull(command, "edit command");
        if ((command.isStartNow() || !startedAt.isEqual(command.getEditedStartedAt())) &&
                Duration.between(startedAt, now()).toMinutes() > 30) {
            throw new BadRequestException("Started at modification can only be made 31 minutes or more before to start");
        }

        this.isSoundOn = command.isSoundOn();
        this.isScreenShow = command.isScreenShow();
        if (isStatusEq(SCHEDULED)) {
            changeStatusAndStartedAt(command.isStartNow(), command.getEditedStartedAt());
        }
        setCategory(command.getEditedCategory());
        setTitle(command.getEditedTitle());
        setThumbnail(command.getEditedThumbnail());
        setNotice(command.getEditedNotice());
    }

    public void start(String url, ZonedDateTime startedAt) {
        requireNonNull(url, "url");
        changeStatus(LIVE);
        this.startedAt = startedAt;
        this.url = url;
    }

    public void finish(BroadcastStatus status, ZonedDateTime endedAt) {
        requireNonNull(endedAt, "endedAt");
        if (status != END && status != CANCEL) {
            throw new BadRequestException(status + " is not a finish status");
        }
        changeStatus(status);
        this.endedAt = endedAt;
    }

    public void changeMessageRoomStatus(boolean canChat) {
        this.canChat = canChat;
    }

    public boolean isStatusEq(@Nullable BroadcastStatus status) {
        return this.status.equals(status);
    }

    public boolean isCreatedByEq(@Nullable Long memberId) {
        return this.memberId.equals(memberId);
    }

    public boolean isCategoryEq(@Nullable Long categoryId) {
        return category.getId().equals(categoryId);
    }

    public boolean isThumbnailUrlEq(@Nullable String thumbnailUrl) {
        return getThumbnailUrl().equals(thumbnailUrl);
    }

    public boolean isStartedAtEq(ZonedDateTime startedAt) {
        return this.startedAt.isEqual(startedAt);
    }

    public String getThumbnailUrl() {
        // FIXME Delete This If After Test
        if (thumbnail.isBlank()) {
            return "https://static-dev.mybeautip.com/common/default/share_square_image.jpg";
        }
        return toUrl(thumbnail, BROADCAST, id);
    }

    public String getThumbnailUrlPath() {
        return BROADCAST.getDirectory(id);
    }

    public int getHeartCount() {
        return statistics.getHeartCount();
    }

    private void initStatusAndStartedAt(boolean isStartNow, ZonedDateTime startedAt) {
        if (isStartNow) readyNow();
        else {
            changeStatus(SCHEDULED);
            setStartedAtBeforeStart(startedAt);
        }
    }

    private void changeStatusAndStartedAt(boolean isStartNow, ZonedDateTime editedStartedAt) {
        if (isStartNow) readyNow();
        else setStartedAtBeforeStart(editedStartedAt);
    }

    private void readyNow() {
        changeStatus(READY);
        setStartedAtBeforeStart(now().plusMinutes(5));
    }

    private void changeStatus(BroadcastStatus changeStatus) {
        requireNonNull(changeStatus, "changeStatus");
        status = status == null ? changeStatus : status.changeTo(changeStatus);
    }

    private void setCategory(BroadcastCategory category) {
        requireNonNull(category, "category");
        if (category.isGroup()) {
            throw new BadRequestException(category + " isn't writable");
        }
        this.category = category;
    }

    private void setTitle(String title) {
        requireNonNull(title, "title");
        validateMaxLengthWithoutWhiteSpace(title, 25, "title");
        this.title = title;
    }

    private void setThumbnail(String thumbnail) {
        requireNonNull(thumbnail, "thumbnail");
        this.thumbnail = getFileName(thumbnail);
    }

    private void setNotice(@Nullable String notice) {
        if (notice != null) {
            validateMaxLengthWithoutWhiteSpace(notice, 100, "notice");
        }
        this.notice = notice;
    }

    // start() will fix startedAt
    private void setStartedAtBeforeStart(ZonedDateTime startedAt) {
        requireNonNull(startedAt, "startedAt");
        if (startedAt.isBefore(now()) || Duration.between(now(), startedAt).toDays() > 14) {
            throw new BadRequestException("Started At cannot be earlier than the current time or later than 14 days");
        }
        if (READY != status && SCHEDULED != status) {
            throw new BadRequestException("Started at can not be edit in status " + status);
        }
        this.startedAt = startedAt;
    }
}
