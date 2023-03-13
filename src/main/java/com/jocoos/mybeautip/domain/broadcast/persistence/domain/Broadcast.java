package com.jocoos.mybeautip.domain.broadcast.persistence.domain;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastEditCommand;
import com.jocoos.mybeautip.global.config.jpa.CreatedAtBaseEntity;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus.*;
import static com.jocoos.mybeautip.global.code.UrlDirectory.BROADCAST;
import static com.jocoos.mybeautip.global.util.FileUtil.getFileName;
import static com.jocoos.mybeautip.global.util.ImageUrlConvertUtil.toUrl;
import static com.jocoos.mybeautip.global.validator.StringValidator.validateMaxLengthWithoutWhiteSpace;
import static java.time.ZonedDateTime.now;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Broadcast extends CreatedAtBaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column
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
    private String pin;

    @Column
    private int viewerCount;

    @Column
    private int maxViewerCount;

    @Column
    private int heartCount;

    @Column
    private int reportCount;

    @Column
    private ZonedDateTime pausedAt;

    @Column
    private ZonedDateTime startedAt;

    @Column
    private ZonedDateTime endedAt;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "category_id")
    private BroadcastCategory category;

    @OrderBy("sorted_username")
    @OneToMany(mappedBy = "broadcast", fetch = LAZY, cascade = ALL, orphanRemoval = true)
    public List<BroadcastViewer> viewerList;

    @Builder
    public Broadcast(@NotNull Long memberId,
                     @NotNull String title,
                     @NotNull String thumbnailUrl,
                     @NotNull ZonedDateTime startedAt,
                     @NotNull BroadcastCategory category,
                     String notice,
                     boolean isStartNow) {
        this.memberId = memberId;
        initStatusAndStartedAt(isStartNow, startedAt);
        setCategory(category);
        setTitle(title);
        setThumbnail(thumbnailUrl);
        setNotice(notice);
    }

    // video and channel key will get from external service so updated later
    public void updateVideoAndChannelKey(@NotNull Long videoKey, @NotNull String chatChannelKey) {
        this.videoKey = videoKey;
        this.chatChannelKey = chatChannelKey;
    }

    public void edit(@NotNull BroadcastEditCommand command) {
        if ((command.isStartNow() || !startedAt.isEqual(command.getEditedStartedAt())) &&
                Duration.between(startedAt, now()).toMinutes() > 30) {
            throw new BadRequestException("");
        }

        changeStatusAndStartedAt(command.isStartNow(), command.getEditedStartedAt());
        setCategory(command.getEditedCategory());
        setTitle(command.getEditedTitle());
        setThumbnail(command.getEditedThumbnail());
        setNotice(command.getEditedNotice());
    }

    public void start(@NotNull String url, @NotNull ZonedDateTime startedAt) {
        changeStatus(LIVE);
        setStartedAt(startedAt);
        this.url = url;
    }

    public void finish(@NotNull BroadcastStatus status, @NotNull ZonedDateTime endedAt) {
        if (status != END && status != CANCEL) {
            throw new BadRequestException(status + " is not a finish status");
        }
        changeStatus(status);
        this.endedAt = endedAt;
    }

    public boolean isCategoryEq(Long categoryId) {
        return category.getId().equals(categoryId);
    }

    public boolean isThumbnailEq(String thumbnailUrl) {
        return getThumbnailUrl().equals(thumbnailUrl);
    }

    public String getThumbnailUrl() {
        if (thumbnail.isBlank()) {
            return "https://static-dev.mybeautip.com/common/default/share_square_image.jpg";
        }
        return toUrl(thumbnail, BROADCAST, id);
    }

    public String getThumbnailUrlPath() {
        return BROADCAST.getDirectory(id);
    }

    private void initStatusAndStartedAt(boolean isStartNow, ZonedDateTime startedAt) {
        if (isStartNow) readyNow();
        else {
            changeStatus(SCHEDULED);
            setStartedAt(startedAt);
        }
    }

    private void changeStatusAndStartedAt(boolean isStartNow, ZonedDateTime editedStartedAt) {
        if (isStartNow) readyNow();
        else setStartedAt(editedStartedAt);
    }

    private void changeStatus(BroadcastStatus changeStatus) {
        status = status == null ? changeStatus : status.changeTo(changeStatus);
    }

    private void readyNow() {
        changeStatus(READY);
        setStartedAt(now().plusMinutes(5));
    }

    private void setCategory(@NotNull BroadcastCategory category) {
        if (category.isGroup()) {
            throw new BadRequestException(category + " isn't writable");
        }
        this.category = category;
    }

    private void setTitle(@NotNull String title) {
        validateMaxLengthWithoutWhiteSpace(title, 25, "title");
        this.title = title;
    }

    private void setThumbnail(@NotNull String thumbnail) {
        this.thumbnail = getFileName(thumbnail);
    }

    private void setNotice(String notice) {
        if (notice != null) {
            validateMaxLengthWithoutWhiteSpace(notice, 100, "notice");
        }
        this.notice = notice;
    }

    private void setStartedAt(@NotNull ZonedDateTime startedAt) {
//        if (startedAt.isBefore(now())) {
//            throw new BadRequestException("Can't Start From Past. Request start_at is " + startedAt);
//        }
        if (Duration.between(now(), startedAt).toDays() > 14) {
            throw new BadRequestException("max duration of startedAt is 14 days");
        }
        this.startedAt = startedAt;
    }
}
