package com.jocoos.mybeautip.domain.video.persistence.repository;

import com.jocoos.mybeautip.domain.video.persistence.domain.VideoCategory;
import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoCategoryRepository extends DefaultJpaRepository<VideoCategory, Integer> {

    List<VideoCategory> findAllBy(Pageable pageable);

    List<VideoCategory> findAllByIdIn(List<Integer> categoryIds);
}
