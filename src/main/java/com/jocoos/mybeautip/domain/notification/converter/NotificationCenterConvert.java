package com.jocoos.mybeautip.domain.notification.converter;

import com.jocoos.mybeautip.domain.notification.code.NotificationLinkType;
import com.jocoos.mybeautip.domain.notification.dto.CenterMessageResponse;
import com.jocoos.mybeautip.domain.notification.persistence.domain.NotificationCenterEntity;
import com.jocoos.mybeautip.domain.notification.vo.NotificationLink;
import com.jocoos.mybeautip.global.util.NotificationConvertUtil;
import com.jocoos.mybeautip.global.util.StringConvertUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.jocoos.mybeautip.global.constant.SignConstant.*;

@Mapper(componentModel = "spring")
public interface NotificationCenterConvert {

    @Mappings({
            @Mapping(target = "status", source = "status"),
            @Mapping(target = "messageType", source = "messageCenter.messageType"),
            @Mapping(target = "imageUrl", source = "imageUrl", qualifiedByName = "justString"),
            @Mapping(target = "message", source = "entity", qualifiedByName = "mergeMessage"),
            @Mapping(target = "notificationLink", source = "entity", qualifiedByName = "mergeNotificationLink")
    })
    CenterMessageResponse convert(NotificationCenterEntity entity);

    List<CenterMessageResponse> convert(List<NotificationCenterEntity> entity);

    @Named("mergeMessage")
    default String mergeMessage(NotificationCenterEntity entity) {
        return convert(entity.getMessageCenter().getMessage(), entity.getArguments());
    }

    @Named("mergeNotificationLink")
    default List<NotificationLink> mergeNotificationLink(NotificationCenterEntity entity) {
        List<NotificationLinkType> typeList = entity.getMessageCenter().getNotificationLinkType();
        if (CollectionUtils.isEmpty(typeList)) {
            return null;
        }

        Map<String, String> argumentMap = StringConvertUtil.convertJsonToMap(entity.getArguments());
        return NotificationConvertUtil.generateNotificationLinkByArguments(typeList, argumentMap);
    }

    @Named("justString")
    default String justString(String str) {
        return str;
    }

    default String convert(final String message, final String argument) {
        Map<String, String> argumentMap = StringConvertUtil.convertJsonToMap(argument);
        return NotificationConvertUtil.generateStringByArguments(message, argumentMap);
    }

    // Message 치환하는 방법.
    // 1. Message 에서 변경할 Text 를 찾아서 Map 에서 꺼내서 치환
    // 2. 그냥 Map Loop 돌면서 Message replace 하는 방법.
    // 처음에 1번 방법으로 하려고 아래 메소드를 만들긴 했는데... 생각해보니 굳이?
    // Argument Map Loop 가 더 빠를 것 같은데? 생각이 듬. 그래서 우선은 사용안함..
    default List<String> findConvertText(final String message) {
        List<String> result = new ArrayList<>();
        int startIndex = 0;
        int endIndex = 0;
        int fromIndex = 0;
        while (message.indexOf(SIGN_DOUBLE_LEFT_BRACE, fromIndex) < 0) {
            startIndex = message.indexOf(SIGN_DOUBLE_LEFT_BRACE, fromIndex);
            endIndex = message.indexOf(SIGN_DOUBLE_RIGHT_BRACE, fromIndex) + SIZE_DOUBLE_INDEX;
            result.add(message.substring(startIndex, endIndex));
            fromIndex = endIndex;
        }

        return result;
    }
}
