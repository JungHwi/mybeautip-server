package com.jocoos.mybeautip.domain.notification.converter;

import com.jocoos.mybeautip.devices.Device;
import com.jocoos.mybeautip.domain.notification.vo.NotificationTargetInfo;
import com.jocoos.mybeautip.member.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {DeviceTokenConvert.class})
public interface MemberNotificationInfoConverter {

    @Mappings({
            @Mapping(target = "memberId", source = "member.id"),
            @Mapping(target = "nickname", source = "member.username"),
            @Mapping(target = "phone", source = "member.phoneNumber"),
            @Mapping(target = "email", source = "member.email"),
            @Mapping(target = "iosDeviceToken", source = "devices"),
            @Mapping(target = "androidDeviceToken", source = "devices")
    })
    NotificationTargetInfo convert(Member member, List<Device> devices);

    default List<NotificationTargetInfo> convert(List<Member> members, List<Device> devices) {
        List<NotificationTargetInfo> result = new ArrayList<>();

        Map<Long, List<Device>> deviceMap = devices.stream()
                .collect(Collectors.groupingBy(device -> device.getCreatedBy().getId()));

        for (Member member : members) {
            result.add(convert(member, deviceMap.get(member.getId())));
        }
        return result;
    }
}
