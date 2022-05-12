package com.jocoos.mybeautip.domain.notification.converter;

import com.jocoos.mybeautip.domain.notification.client.vo.AppPushMessage;
import com.jocoos.mybeautip.domain.notification.persistence.domain.NotificationMessagePushEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface NotificationMessagePushConverter {

    @Mappings({
            @Mapping(target = "messageType", source = "entity.messageType"),
            @Mapping(target = "title", source = "entity.title"),
            @Mapping(target = "message", source = "entity.message"),
            @Mapping(target = "deepLink", source = "entity.deepLink"),
            @Mapping(target = "imageUrl", source = "imageUrl"),
    })
    AppPushMessage convert(NotificationMessagePushEntity entity, String imageUrl);

    @Mapping(target = "imageUrl", ignore = true)
    AppPushMessage convert(NotificationMessagePushEntity entity);
}