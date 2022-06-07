package com.jocoos.mybeautip.domain.notification.converter;

import com.jocoos.mybeautip.domain.notification.persistence.domain.NotificationTemplateEntity;
import com.jocoos.mybeautip.domain.notification.vo.NotificationTemplate;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationTemplateConverter {

    NotificationTemplate convert(NotificationTemplateEntity entity);

    List<NotificationTemplate> convert(List<NotificationTemplateEntity> entityList);
}
