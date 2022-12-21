package com.jocoos.mybeautip.devices;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HealthCheckRepository extends JpaRepository<HealthCheck, Long> {

    List<HealthCheck> findByOs(String os);
}
