package com.jocoos.mybeautip.app;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AppInfoRepository extends JpaRepository<AppInfo, Long> {
  Optional<AppInfo> findByOs(String os);
}
