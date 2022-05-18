package com.jocoos.mybeautip.domain.notification.vo;

import com.jocoos.mybeautip.domain.notification.code.NotificationLinkType;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationLink {

    private NotificationLinkType type;
    private String parameter;
}
