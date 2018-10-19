package com.jocoos.mybeautip.app;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppInfoRepository extends JpaRepository<AppInfo, Long> {
  List<AppInfo> findByOs(String os, Pageable pageable);
}
