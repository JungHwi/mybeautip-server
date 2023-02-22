package com.jocoos.mybeautip.domain.broadcast.persistence.domain;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastCategoryType;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.global.config.jpa.CreatedAtBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
public class Broadcast extends CreatedAtBaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Enumerated(STRING)
    private BroadcastStatus status;

    // generated column
    @Column(insertable = false, updatable = false)
    private int sortedStatus;

    @Column
    private String videoKey;

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
    private int heartCount = 0;

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

    public BroadcastCategoryType getCategoryType() {
        return category.getType();
    }

    public String getThumbnailUrl() {
        return null;
    }
}
