package com.jocoos.mybeautip.notification.event;

import com.jocoos.mybeautip.admin.AdminNotificationController;
import com.jocoos.mybeautip.audit.CreatedDateAuditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import javax.persistence.*;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "push_messages")
public class PushMessage extends CreatedDateAuditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Integer category;   // 1: instant, ...

    @Column
    private Integer platform; // 0: all, 1: ios, 2: android

    @Column
    private String resourceType;

    @Column
    private String resourceIds;

    @Column
    private String title;

    @Column(nullable = false)
    private String body;

    @Column
    private Integer targetDeviceCount;

    @Column
    private Integer successCount;

    @Column
    private Integer failCount;

    public PushMessage(AdminNotificationController.NotificationRequest request,
                       int targetDeviceCount, int successCount, int failCount) {
        BeanUtils.copyProperties(request, this);
        this.body = request.getMessage();
        this.targetDeviceCount = targetDeviceCount;
        this.successCount = successCount;
        this.failCount = failCount;
    }
}
