package com.jocoos.mybeautip.devices;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, String> {

  List<Device> findByCreatedById(Long created);
  
  List<Device> findByCreatedByIdAndValidIsTrue(Long created);

  Page<Device> findByPushableAndCreatedByDeletedAtIsNull(boolean pushable, Pageable pageable);

  Page<Device> findByCreatedByIdAndCreatedByPushableAndCreatedByDeletedAtIsNull(Long memberId, boolean pushable, Pageable pageable);

  Page<Device> findByPushableAndOsAndCreatedByDeletedAtIsNull(boolean pushable, String os, Pageable pageable);

  Page<Device> findByCreatedByLinkInAndPushableAndCreatedByDeletedAtIsNullAndCreatedByUsernameContaining(Collection<Integer> links, boolean pushable, String username, Pageable pageable);

  Page<Device> findByCreatedByLinkInAndPushableAndOsAndCreatedByDeletedAtIsNullAndCreatedByUsernameContaining(Collection<Integer> links, boolean pushable, String os, String username, Pageable pageable);

  Page<Device> findByCreatedByLinkInAndPushableAndCreatedByDeletedAtIsNull(Collection<Integer> links, boolean pushable, Pageable pageable);

  Page<Device> findByCreatedByLinkInAndPushableAndOsAndCreatedByDeletedAtIsNull(Collection<Integer> links, boolean pushable, String os, Pageable pageable);

}
