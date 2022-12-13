package com.jocoos.mybeautip.video;

import com.jocoos.mybeautip.domain.video.code.VideoStatus;
import com.jocoos.mybeautip.domain.video.persistence.domain.VideoCategory;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.member.Member;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.jocoos.mybeautip.domain.video.code.VideoStatus.DELETE;
import static com.jocoos.mybeautip.domain.video.code.VideoStatus.OPEN;
import static com.jocoos.mybeautip.global.util.date.ZonedDateTimeUtil.toUTCZoned;
import static com.jocoos.mybeautip.video.Visibility.PRIVATE;
import static com.jocoos.mybeautip.video.Visibility.PUBLIC;
import static java.lang.Boolean.TRUE;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "videos")
public class Video {
    @Column
    public Date startedAt;
    @Column
    public Date endedAt;
    @Column
    @CreatedDate
    public Date createdAt;
    @Column
    @LastModifiedDate
    public Date modifiedAt;
    @Column
    public Date deletedAt;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String videoKey;
    @Column(nullable = false)
    private String type;
    @Column(nullable = false)
    private String state;
    @Column(nullable = false)
    private Boolean locked;
    @Column(nullable = false)
    private Boolean muted;
    @Column(nullable = false)
    private String visibility;
    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VideoCategoryMapping> categoryMapping;
    @Column
    private String title;
    @Column
    private String content;
    @Column
    private String url;
    @Column
    private String originalFilename;
    @Column
    private String thumbnailPath;
    @Column
    private String thumbnailUrl;
    @Column
    private String chatRoomId;
    @Column(nullable = false)
    private int duration;
    @Column
    private String liveKey;
    @Column
    private String outputType;
    @Column
    private String data;
    @Column(nullable = false)
    private Integer watchCount;
    @Column(nullable = false)
    private Integer totalWatchCount;
    @Column(nullable = false)
    private Integer heartCount;
    @Column(nullable = false)
    private Integer viewCount;
    @Column(nullable = false)
    private Integer likeCount;
    @Column(nullable = false)
    private Integer scrapCount;
    @Column(nullable = false)
    private Integer commentCount;
    @Column(nullable = false)
    private Integer orderCount;
    @Column
    private Long reportCount;
    @Column(nullable = false)
    private Integer relatedGoodsCount;
    @Column(nullable = false)
    private String relatedGoodsThumbnailUrl;
    @ManyToOne
    @JoinColumn(name = "owner")
    private Member member;

    // is_top_fix true 인것만 정렬 가능
    @Column
    private Integer sorting;

    @Column
    private Boolean isTopFix;

    @Column
    private Boolean isRecommended;

    @Enumerated(EnumType.STRING)
    private VideoStatus status;

    @Transient
    private boolean isFirstOpen;

    public Video(Member owner) {
        this.member = owner;
        this.videoKey = "";
        this.state = "CREATED";
        this.url = "";
        this.thumbnailPath = "";
        this.thumbnailUrl = "";
        this.relatedGoodsCount = 0;
        this.relatedGoodsThumbnailUrl = "";
        this.commentCount = 0;
        this.heartCount = 0;
        this.likeCount = 0;
        this.watchCount = 0;
        this.totalWatchCount = 0;
        this.viewCount = 0;
        this.orderCount = 0;
        this.reportCount = 0L;
        this.scrapCount = 0;
    }

    public String getCategoryNames() {
        return categoryMapping != null && !categoryMapping.isEmpty() ? categoryMapping.stream().map(m -> m.getVideoCategory().getTitle()).collect(Collectors.joining(", ")) : "없음";
    }

    public List<Integer> getCategoryId() {
        return categoryMapping != null && !categoryMapping.isEmpty() ? categoryMapping.stream().map(m -> m.getVideoCategory().getId()).collect(Collectors.toList()) : new ArrayList<>();
    }

    public void setCategoryMapping(List<VideoCategoryMapping> categoryMapping) {
        if (this.categoryMapping != null) {
            this.categoryMapping.clear();
        }
        if (categoryMapping != null) {
            if (this.categoryMapping == null) {
                this.categoryMapping = new ArrayList<>();
            }
            this.categoryMapping.addAll(categoryMapping);
        }
    }

    public ZonedDateTime getCreatedAtZoned() {
        return toUTCZoned(createdAt);
    }

    public List<VideoCategory> getCategories() {
        return categoryMapping.stream()
                .map(VideoCategoryMapping::getVideoCategory)
                .toList();
    }

    public void hide(boolean isHide) {
        this.visibility = isHide ? PRIVATE.name() : PUBLIC.name();
    }

    public void delete() {
        this.visibility = PRIVATE.name();
        this.status = DELETE;
        this.deletedAt = new Date();
        disableFixAndRecommend();
    }

    public void recommend(boolean isRecommended) {
        if (isRecommended) {
            validNotDelete();
        }
        this.isRecommended = isRecommended;
    }

    public void validNotDelete() {
        if (deletedAt != null) {
            throw new BadRequestException("삭제된 영상입니다.");
        }
    }

    private void disableFixAndRecommend() {
        this.isTopFix = false;
        this.isRecommended = false;
        this.sorting = null;
    }

    public Boolean isTopFixTrueOrNull() {
        return TRUE.equals(isTopFix) ? isTopFix : null;
    }

    public Boolean isRecommendedTrueOrNull() {
        return TRUE.equals(isRecommended) ? isRecommended : null;
    }

    public boolean isOpenAndVisible() {
        return visibility.equals(PUBLIC.name()) && status.equals(OPEN);
    }

    public boolean isPublic() {
        return visibility.equals(PUBLIC.name());
    }

    public ZonedDateTime getStartedAtZoned() {
        return toUTCZoned(startedAt);
    }
}
