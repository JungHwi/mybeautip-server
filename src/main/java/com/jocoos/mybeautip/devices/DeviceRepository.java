package com.jocoos.mybeautip.devices;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface DeviceRepository extends JpaRepository<Device, String> {

  List<Device> findByCreatedById(Long created);

  List<Device> findByCreatedByIdAndValidIsTrue(Long created);

  List<Device> findByPushableAndValid(boolean pushable, boolean valid);
  
  List<Device> findByPushableAndValidAndOs(boolean pushable, boolean valid, String os);

  List<Device> findByPushableAndValidAndCreatedByPushable(boolean pushable, boolean valid, boolean memberPushable);

  List<Device> findByCreatedByIdAndPushableAndValidAndCreatedByPushable(Long memberId, boolean pushable, boolean vaild, boolean memberPushable);

  List<Device> findByCreatedByIdInAndPushableAndValidAndCreatedByPushable(List<Long> memberId, boolean pushable, boolean vaild, boolean memberPushable);

  List<Device> findByCreatedByIdAndOs(Long created, String os);

  Page<Device> findByCreatedByLinkInAndPushableAndValidAndCreatedByDeletedAtIsNullAndCreatedByUsernameContaining(Collection<Integer> links, boolean pushable, boolean valid, String username, Pageable pageable);

  Page<Device> findByCreatedByLinkInAndPushableAndValidAndOsAndCreatedByDeletedAtIsNullAndCreatedByUsernameContaining(Collection<Integer> links, boolean pushable, boolean valid, String os, String username, Pageable pageable);

  Page<Device> findByCreatedByLinkInAndPushableAndValidAndCreatedByDeletedAtIsNull(Collection<Integer> links, boolean pushable, boolean valid, Pageable pageable);

  Page<Device> findByCreatedByLinkInAndPushableAndValidAndOsAndCreatedByDeletedAtIsNull(Collection<Integer> links, boolean pushable, boolean valid, String os, Pageable pageable);

}