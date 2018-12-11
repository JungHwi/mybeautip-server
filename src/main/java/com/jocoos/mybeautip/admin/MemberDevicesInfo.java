package com.jocoos.mybeautip.admin;

import java.util.List;

import org.springframework.beans.BeanUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.devices.Device;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.restapi.DeviceController;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
public class MemberDevicesInfo extends MemberDetailInfo {

  private int deivceCount;
  private List<DeviceController.DeviceInfo> devices;

  public MemberDevicesInfo(Member member) {
    BeanUtils.copyProperties(member, this);
  }

  public void setDevices(List<DeviceController.DeviceInfo> devices) {
    if (devices != null && devices.size() > 0) {
      this.devices = devices;
      this.deivceCount = devices.size();
    }
  }
}
