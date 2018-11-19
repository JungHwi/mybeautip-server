package com.jocoos.mybeautip.devices;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

  List<Notice> findByOs(String os);
}
