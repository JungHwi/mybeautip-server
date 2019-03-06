package com.jocoos.mybeautip.notification;

import java.util.List;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import lombok.extern.slf4j.Slf4j;

import static com.google.common.base.Preconditions.checkNotNull;

import com.jocoos.mybeautip.admin.AdminNotificationController;
import com.jocoos.mybeautip.devices.Device;
import com.jocoos.mybeautip.devices.DeviceService;
import com.jocoos.mybeautip.video.Video;

@Slf4j
@Configurable
public class InstantNotificationTask implements Runnable {

  @Value("${mybeautip.notification.instant-message.platform}")
  private int platform;

  private DeviceService deviceService;
  private Video video;
  private String title;
  private String message;

  public InstantNotificationTask(DeviceService deviceService, Video video, String message, String title) {
    this.deviceService = deviceService;
    this.video = checkNotNull(video);
    this.message = checkNotNull(message);
    this.title = title;
  }

  @Override
  public void run() {
    log.debug("platform: {}", platform);
    log.debug("video: {}", video);

    List<Device> devices = deviceService.getDevices(platform);
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
