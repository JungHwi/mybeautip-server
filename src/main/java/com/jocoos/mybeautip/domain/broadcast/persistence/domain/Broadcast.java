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
import javax.persistence.*;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus.*;
import static com.jocoos.mybeautip.global.code.UrlDirectory.BROADCAST;
import static com.jocoos.mybeautip.global.util.FileUtil.getFileName;
import static com.jocoos.mybeautip.global.util.ImageUrlConvertUtil.toUrl;
import static com.jocoos.mybeautip.global.validator.StringValidator.validateMaxLengthWithoutWhiteSpace;
import static java.time.ZonedDateTime.now;
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
    private Boolean canChat;

    @Column
    private Boolean isSoundOn;

    @Column
    private Boolean isScreenShow;

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

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "category_id")
    private BroadcastCategory category;

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

    public List<Long> syncViewer(List<BroadcastViewerVo> newViewers) {
        return this.viewerList.sync(this, newViewers);
    }

    // video and channel key will get from external service so updated later
    public void updateVideoAndChannelKey(Long videoKey, String chatChannelKey) {
        this.videoKey = videoKey;
        this.chatChannelKey = chatChannelKey;
    }

    public void edit(BroadcastEditCommand command) {
        if ((command.isStartNow() || !startedAt.isEqual(command.getEditedStartedAt())) &&
                Duration.between(startedAt, now()).toMinutes() > 30) {
            throw new BadRequestException("");
        }

        this.isSoundOn = command.isSoundOn();
        this.isScreenShow = command.isScreenShow();
        changeStatusAndStartedAt(command.isStartNow(), command.getEditedStartedAt());
        setCategory(command.getEditedCategory());
        setTitle(command.getEditedTitle());
        setThumbnail(command.getEditedThumbnail());
        setNotice(command.getEditedNotice());
    }

    public void start(String url, ZonedDateTime startedAt) {
        changeStatus(LIVE);
        setStartedAt(startedAt);
        this.url = url;
    }

    public void finish(BroadcastStatus status, ZonedDateTime endedAt) {
        if (status != END && status != CANCEL) {
            throw new BadRequestException(status + " is not a finish status");
        }
        changeStatus(status);
        this.endedAt = endedAt;
    }

    public void changeChatStatus(boolean canChat) {
        this.canChat = canChat;
    }

    public boolean isCreatedByEq(Long memberId) {
        return this.memberId.equals(memberId);
    }

    public boolean isCategoryEq(Long categoryId) {
        return category.getId().equals(categoryId);
    }

    public boolean isThumbnailUrlEq(String thumbnailUrl) {
        return getThumbnailUrl().equals(thumbnailUrl);
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

    private void setCategory(BroadcastCategory category) {
        if (category.isGroup()) {
            throw new BadRequestException(category + " isn't writable");
        }
        this.category = category;
    }

    private void setTitle(String title) {
        validateMaxLengthWithoutWhiteSpace(title, 25, "title");
        this.title = title;
    }

    private void setThumbnail(String thumbnail) {
        this.thumbnail = getFileName(thumbnail);
    }

    private void setNotice(String notice) {
        if (notice != null) {
            validateMaxLengthWithoutWhiteSpace(notice, 100, "notice");
        }
        this.notice = notice;
    }

    private void setStartedAt(ZonedDateTime startedAt) {
        if (Duration.between(now(), startedAt).toDays() > 14) {
            throw new BadRequestException("Max duration of startedAt is 14 days");
        }
        this.startedAt = startedAt;
    }
}
