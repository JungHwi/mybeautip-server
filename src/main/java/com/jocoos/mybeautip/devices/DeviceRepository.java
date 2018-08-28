package com.jocoos.mybeautip.devices;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, String> {

  List<Device> findByCreatedById(Long created);
}
