package com.jocoos.mybeautip.devices;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, String> {

  List<Device> findByCreatedById(Long created);

  List<Device> findByCreatedByIdAndValidIsTrue(Long created);

  Page<Device> findByPushableAndValid(boolean pushable, boolean vaild, Pageable pageable);

  Page<Device> findByPushableAndValidAndOs(boolean pushable, boolean vaild, String os, Pageable pageable);

  Page<Device> findByCreatedByIdAndPushableAndValid(Long memberId, boolean pushable, boolean vaild, Pageable pageable);

  List<Device> findByCreatedByIdAndOs(Long created, String os);

  Page<Device> findByCreatedByLinkInAndPushableAndValidAndCreatedByDeletedAtIsNullAndCreatedByUsernameContaining(Collection<Integer> links, boolean pushable, boolean valid, String username, Pageable pageable);

  Page<Device> findByCreatedByLinkInAndPushableAndValidAndOsAndCreatedByDeletedAtIsNullAndCreatedByUsernameContaining(Collection<Integer> links, boolean pushable, boolean valid, String os, String username, Pageable pageable);

  Page<Device> findByCreatedByLinkInAndPushableAndValidAndCreatedByDeletedAtIsNull(Collection<Integer> links, boolean pushable, boolean valid, Pageable pageable);

  Page<Device> findByCreatedByLinkInAndPushableAndValidAndOsAndCreatedByDeletedAtIsNull(Collection<Integer> links, boolean pushable, boolean valid, String os, Pageable pageable);

}