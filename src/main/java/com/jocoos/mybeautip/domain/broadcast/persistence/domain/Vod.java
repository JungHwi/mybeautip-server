package com.jocoos.mybeautip.domain.broadcast.persistence.domain;

import com.jocoos.mybeautip.global.config.jpa.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
    private String videoKey;

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

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "category_id")
    private BroadcastCategory category;

    @Builder
    public Vod(String videoKey,
               String title,
               BroadcastCategory category,
               int liveHeartCount,
               long memberId) {
        this.videoKey = videoKey;
        this.title = title;
        this.category = category;
        this.liveHeartCount = liveHeartCount;
        this.memberId = memberId;
        this.isVisible = false;
    }

    public void visible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public void addReportCount(int addCount) {
        this.reportCount += addCount;
    }

    public void addHeartCount(int addCount) {
        this.vodHeartCount += addCount;
    }

    // TODO Need to check if the URL can be generated by the video key
    public String getUrl() {
        return null;
    }

    // TODO Need to check how thumbnail generate by Flipflop
    public String getThumbnailUrl() {
        return "ㅇ";
    }
}
