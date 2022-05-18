package com.jocoos.mybeautip.domain.member.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SettingNotificationDto {

    private boolean pushable;
}
