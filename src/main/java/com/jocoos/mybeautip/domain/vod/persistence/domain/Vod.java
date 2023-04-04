package com.jocoos.mybeautip.domain.vod.persistence.domain;

import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastCategory;
import com.jocoos.mybeautip.domain.vod.code.VodStatus;
import com.jocoos.mybeautip.global.config.jpa.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.domain.vod.code.VodStatus.AVAILABLE;
import static com.jocoos.mybeautip.global.code.UrlDirectory.BROADCAST;
import static com.jocoos.mybeautip.global.util.FileUtil.getFileName;
import static com.jocoos.mybeautip.global.util.ImageUrlConvertUtil.toUrl;
import static com.jocoos.mybeautip.global.validator.StringValidator.validateMaxLengthWithoutWhiteSpace;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Vod extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column
    private Long videoKey;

    @Enumerated(STRING)
    private VodStatus status;

    @Column
    private String title;

    @Column
    private String thumbnail;

    @Column
    private int duration;

    @Column
    private int viewCount;

    @Column
    private int reportCount;

    // generated column
    @Column(insertable = false, updatable = false)
    private int totalHeartCount;

    @Column(updatable = false)
    private int liveHeartCount;

    @Column
    private int vodHeartCount;

    @Column(nullable = false)
    private long memberId;

    @Column
    private boolean isVisible;

    @Column
    private String chatChannelKey;

    @Column
    private ZonedDateTime chatStartedAt;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "category_id")
    private BroadcastCategory category;

    @Transient
    private String thumbnailUrl;

    @Builder
    public Vod(Long videoKey,
               String chatChannelKey,
               String title,
               String thumbnailUrl,
               BroadcastCategory category,
               VodStatus status,
               boolean isVisible,
               long memberId) {
        this.videoKey = videoKey;
        this.chatChannelKey = chatChannelKey;
        this.memberId = memberId;
        this.category = category;
        this.status = status;
        this.isVisible = isVisible;
        setTitle(title);
        setThumbnail(thumbnailUrl);
    }

    public void changeStatus(VodStatus status) {
        this.status = status;
    }

    public void changeVisibility(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public void integrate(ZonedDateTime startedAt) {
        this.chatStartedAt = startedAt;
    }

    public boolean canWatch() {
        return isVisible && status == AVAILABLE;
    }

    public void addHeartCount(int addCount) {
        this.vodHeartCount += addCount;
    }

    // TODO Need to check if the URL can be generated by the video key
    public String getUrl() {
        return "tempUrl";
    }

    public String getThumbnailUrl() {
        if (thumbnailUrl == null) {
            thumbnailUrl = toUrl(thumbnail, BROADCAST, id);
        }
        return thumbnailUrl;
    }

    public void edit(String title,
                     String thumbnailUrl,
                     Boolean isVisible) {
        if (title != null) setTitle(title);
        if (thumbnail != null) setThumbnail(thumbnailUrl);
        if (isVisible != null) this.isVisible = isVisible;
    }

    private void setTitle(String title) {
        validateMaxLengthWithoutWhiteSpace(title, 25, "title");
        this.title = title;
    }

    private void setThumbnail(@NotNull String thumbnailUrl) {
        this.thumbnail = getFileName(thumbnailUrl);
    }
}
