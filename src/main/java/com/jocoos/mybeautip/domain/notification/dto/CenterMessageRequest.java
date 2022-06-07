package com.jocoos.mybeautip.domain.notification.dto;

import com.jocoos.mybeautip.domain.notification.code.TemplateType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CenterMessageRequest {

    private List<Long> userIds;

    private TemplateType type;

}
