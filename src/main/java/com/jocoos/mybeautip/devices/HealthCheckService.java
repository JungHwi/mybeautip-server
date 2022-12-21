package com.jocoos.mybeautip.devices;

import org.springframework.data.util.Version;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HealthCheckService {

    private final HealthCheckRepository healthCheckRepository;

    public HealthCheckService(HealthCheckRepository healthCheckRepository) {
        this.healthCheckRepository = healthCheckRepository;
    }

    public List<HealthCheck> findByOs(String os, String version) {
        return healthCheckRepository.findByOs(os).stream()
                .filter(input -> {
                    Version request = Version.parse(version);
                    Version min = Version.parse(input.getMinVersion());

                    if (!min.isLessThanOrEqualTo(request)) {
                        return false;
                    }

                    Version max = Version.parse(input.getMaxVersion());
                    return max.isGreaterThanOrEqualTo(request);
                }).collect(Collectors.toList());
    }
}
