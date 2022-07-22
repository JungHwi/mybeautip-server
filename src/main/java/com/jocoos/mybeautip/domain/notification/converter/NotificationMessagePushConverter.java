package com.jocoos.mybeautip.domain.notification.converter;

import com.jocoos.mybeautip.domain.notification.client.vo.AppPushMessage;
import com.jocoos.mybeautip.domain.notification.persistence.domain.NotificationMessagePushEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface NotificationMessagePushConverter {

    @Mappings({
            @Mapping(target = "title", source = "entity.title"),
            @Mapping(target = "message", source = "entity.message"),
            @Mapping(target = "notificationLinkType", source = "entity.notificationLinkType"),
            @Mapping(target = "imageUrl", source = "imageUrl"),
            @Mapping(target = "notificationId", source = "notificationId"),
            @Mapping(target = "notificationLink", ignore = true)
    })
    AppPushMessage convert(NotificationMessagePushEntity entity, String imageUrl, Long notificationId);

    @Mappings({
            @Mapping(target = "title", source = "entity.title"),
            @Mapping(target = "message", source = "entity.message"),
            @Mapping(target = "notificationLinkType", source = "entity.notificationLinkType"),
            @Mapping(target = "notificationId", source = "notificationId"),
            @Mapping(target = "imageUrl", ignore = true),
            @Mapping(target = "notificationLink", ignore = true),
    })
    AppPushMessage convert(NotificationMessagePushEntity entity, long notificationId);

}