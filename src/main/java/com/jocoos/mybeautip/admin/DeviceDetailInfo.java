package com.jocoos.mybeautip.admin;

import java.util.Date;

import org.springframework.beans.BeanUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.devices.Device;
import com.jocoos.mybeautip.restapi.DeviceController;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
public class DeviceDetailInfo extends DeviceController.DeviceInfo {
  private boolean valid;
  private Date createdAt;
  private MemberDetailInfo member;

  public DeviceDetailInfo(Device device) {
    super(device);
    BeanUtils.copyProperties(device, this);
  }

  public DeviceDetailInfo(Device device, MemberDetailInfo member) {
    this(device);
    this.member = member;
  }
}
