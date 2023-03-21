package com.jocoos.mybeautip.domain.notification.aspect.service.impl;

import com.jocoos.mybeautip.domain.notification.aspect.service.AspectNotificationService;
import com.jocoos.mybeautip.domain.notification.code.NotificationArgument;
import com.jocoos.mybeautip.domain.notification.code.TemplateType;
import com.jocoos.mybeautip.domain.notification.service.NotificationSendService;
import com.jocoos.mybeautip.video.Video;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.jocoos.mybeautip.domain.notification.code.TemplateType.VIDEO_UPLOAD;


@Slf4j
@Component
@RequiredArgsConstructor
public class VideoUploadNotificationService implements AspectNotificationService<Video> {

    private static final TemplateType TEMPLATE_TYPE = VIDEO_UPLOAD;
    private final NotificationSendService sendService;


    @Override
    public TemplateType getTemplateType() {
        return TEMPLATE_TYPE;
    }

    @Async
    @Override
    public void occurs(Object object) {
        if (object instanceof Video video && video.isOpenAndPublic()) {
                send(video);
        }
    }

    @Override
    public void send(Video video) {
        Map<String, String> arguments = getArgument(video);
        sendService.sendAll(TEMPLATE_TYPE, video.getThumbnailUrl(), arguments);
    }

    private Map<String, String> getArgument(Video video) {
        Map<String, String> arguments = new HashMap<>();
        arguments.put(NotificationArgument.VIDEO_ID.name(), String.valueOf(video.getId()));
        return arguments;
    }
}
