package com.jocoos.mybeautip.domain.broadcast.persistence.domain;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.global.config.jpa.CreatedAtBaseEntity;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;

import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastCategoryType.GROUP;
import static com.jocoos.mybeautip.global.code.UrlDirectory.BROADCAST;
import static com.jocoos.mybeautip.global.util.FileUtil.getFileName;
import static com.jocoos.mybeautip.global.util.ImageUrlConvertUtil.toUrl;
import static com.jocoos.mybeautip.global.validator.StringValidator.validateMaxLengthWithoutWhiteSpace;
import static java.util.Objects.requireNonNull;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Entity
@Getter
public class Broadcast extends CreatedAtBaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column
    private Long videoKey;

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
    public Broadcast(long videoKey,
                     long memberId,
                     BroadcastStatus status,
                     String title,
                     String thumbnail,
                     String notice,
                     ZonedDateTime startedAt,
                     BroadcastCategory category) {
        requireNonNull(startedAt);
        requireNonNull(category);
        this.videoKey = videoKey;
        this.memberId = memberId;
        this.status = status;
        this.startedAt = startedAt;
        setCategory(category);
        setTitle(title);
        setThumbnail(thumbnail);
        setNotice(notice);
    }

    public void edit(String editedTitle, String editedNotice, String editedThumbnail) {
        setTitle(editedTitle);
        setThumbnail(editedThumbnail);
        setNotice(editedNotice);
    }

    // TODO Live Url Setting Not Yet Confirmed
    public String getUrl() {
        return "tempUrl";
    }

    public String getThumbnailUrl() {
        return toUrl(thumbnail, BROADCAST, id);
    }

    private void setCategory(BroadcastCategory category) {
        if (category.isType(GROUP)) {
            throw new BadRequestException(category + " isn't writable");
        }
        this.category = category;
    }

    private void setTitle(String title) {
        requireNonNull(title);
        validateMaxLengthWithoutWhiteSpace(title, 25, "title");
        this.title = title;
    }

    private void setThumbnail(String thumbnail) {
        requireNonNull(thumbnail);
        this.thumbnail = getFileName(thumbnail);
    }

    private void setNotice(String notice) {
        if (notice != null) {
            validateMaxLengthWithoutWhiteSpace(notice, 100, "notice");
        }
        this.notice = notice;
    }

    public void shutdown() {
        this.status = status.toEnd();
    }
}
