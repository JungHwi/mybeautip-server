package com.jocoos.mybeautip.notification;

import com.jocoos.mybeautip.admin.AdminNotificationController;
import com.jocoos.mybeautip.devices.Device;
import com.jocoos.mybeautip.devices.DeviceService;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.video.Video;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Configurable
public class InstantNotificationTask implements Runnable {

    @Value("${mybeautip.notification.instant-message.platform}")
    private int platform;

    private DeviceService deviceService;
    private Video video;
    private String title;
    private String message;
    private List<Member> excludes;

    public InstantNotificationTask(DeviceService deviceService, Video video, String message, String title, List<Member> excludes) {
        this.deviceService = deviceService;
        this.video = Objects.requireNonNull(video);
        this.message = Objects.requireNonNull(message);
        this.title = title;
        this.excludes = excludes;

    }

    @Override
    public void run() {
        log.debug("platform: {}", platform);
        log.debug("video: {}", video);

        List<Device> devices = deviceService.getDevices(platform).stream()
                .filter(device -> !excludes.contains(device.getCreatedBy())).collect(Collectors.toList());
        AdminNotificationController.NotificationRequest request = createRequest();
        deviceService.pushAll(devices, request);
    }

    private AdminNotificationController.NotificationRequest createRequest() {
        AdminNotificationController.NotificationRequest request = new AdminNotificationController.NotificationRequest();
        request.setPlatform(platform);
        request.setCategory(1);
        request.setTitle(title);
        request.setMessage(message);
        request.setResourceType(Notification.RESOURCE_TYPE_VIDEO);
        request.setResourceIds(String.valueOf(video.getId()));
        request.setImageUrl(video.getThumbnailUrl());
        return request;
    }
}
