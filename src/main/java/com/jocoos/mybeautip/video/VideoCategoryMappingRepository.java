package com.jocoos.mybeautip.video;


import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoCategoryMappingRepository extends JpaRepository<VideoCategoryMapping, Long> {

    void deleteByVideoId(Long videoId);
}
