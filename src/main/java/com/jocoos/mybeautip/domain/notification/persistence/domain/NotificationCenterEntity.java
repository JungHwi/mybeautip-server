package com.jocoos.mybeautip.domain.notification.persistence.domain;

import com.jocoos.mybeautip.domain.notification.code.NotificationStatus;
import com.jocoos.mybeautip.global.config.jpa.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "notification_center")
public class NotificationCenterEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private long id;

    @Column
    private long userId;

    @Column
    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    @Column(name = "message_id")
    private long messageId;

    @Column(length = 200)
    private String arguments;

    @Column(length = 200)
    private String imageUrl;

    @ManyToOne(optional = false)
    @JoinColumn(name = "message_id", insertable = false, updatable = false)
    private NotificationMessageCenterEntity messageCenter;
}
