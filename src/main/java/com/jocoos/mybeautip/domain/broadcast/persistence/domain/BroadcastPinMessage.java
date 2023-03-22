package com.jocoos.mybeautip.domain.broadcast.persistence.domain;

import com.jocoos.mybeautip.global.config.jpa.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Setter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class BroadcastPinMessage extends BaseEntity {

    @Id
    private Long broadcastId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "broadcast_id")
    private Broadcast broadcast;

    private Long memberId;

    private String username;

    private String avatarUrl;

    private Long messageId;

    private String message;

    @Builder
    public BroadcastPinMessage(Broadcast broadcast,
                               Long memberId,
                               String username,
                               String avatarUrl,
                               Long messageId,
                               String message) {
        this.broadcast = broadcast;
        this.memberId = memberId;
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.messageId = messageId;
        this.message = message;
    }
}
