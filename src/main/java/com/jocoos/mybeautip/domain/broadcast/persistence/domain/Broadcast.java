package com.jocoos.mybeautip.domain.broadcast.persistence.domain;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.global.config.jpa.CreatedAtBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
public class Broadcast extends CreatedAtBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long categoryId;

    @Enumerated(EnumType.STRING)
    private BroadcastStatus status;

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
    private int heartCount = 0;

    @Column
    private ZonedDateTime startedAt;

    @Column
    private ZonedDateTime endedAt;

    @OrderBy("sorted_username")
    @OneToMany(mappedBy = "broadcast", fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, orphanRemoval = true)
    public List<BroadcastViewer> viewerList;
}
