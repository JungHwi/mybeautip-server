package com.jocoos.mybeautip.domain.broadcast.persistence.domain;

import com.jocoos.mybeautip.global.config.jpa.BaseEntity;
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
    private String title;

    @Column
    private String thumbnail;

    @Column
    private boolean isVisible;

    @Column
    private int duration;

    @Column
    private int viewCount;

    @Column
    private int reportCount;

    // generated column
    @Column(insertable = false, updatable = false)
    private int totalHeartCount;

    @Column(insertable = false, updatable = false)
    private int liveHeartCount;

    @Column
    private int vodHeartCount;

    @Column(nullable = false)
    private long memberId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "category_id")
    private BroadcastCategory category;

    public String getThumbnailUrl() {
        return null;
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
}
