package com.jocoos.mybeautip.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.video.VideoRepository;
import com.jocoos.mybeautip.video.watches.VideoWatchService;

@Slf4j
@RestController
@RequestMapping("/api/admin/videos")
public class VideoStatsController {


  private final VideoRepository videoRepository;
  private final VideoWatchService videoWatchService;

  public VideoStatsController(VideoRepository videoRepository,
                              VideoWatchService videoWatchService) {
    this.videoRepository = videoRepository;
    this.videoWatchService = videoWatchService;
  }

  @GetMapping("/{id:.+}/watches")
  public ResponseEntity<VideoWatchStatsInfo> getWatchStats(@PathVariable Long id) {

    videoRepository.findById(id).orElseThrow(() -> new NotFoundException("video_not_found", ""));

    Map<Integer, Integer[]> stats = videoWatchService.getVideoWatchStats(id);
    VideoWatchStatsInfo statsInfo = new VideoWatchStatsInfo();
    stats.forEach((k, v) -> statsInfo.add(k, v));

    log.info("stats: {}", stats);
    return new ResponseEntity<>(statsInfo, HttpStatus.OK);
  }

  @Data
  public static class VideoWatchStatsInfo {
    private List<VideoWatchGraph> total;
    private List<VideoWatchGraph> watcher;
    private List<VideoWatchGraph> guest;

    public VideoWatchStatsInfo() {
      this.total = new ArrayList<>();
      this.watcher = new ArrayList<>();
      this.guest = new ArrayList<>();
    }

    public void add(int key, Integer[] counts) {
      this.total.add(new VideoWatchGraph(key, counts[0]));
      this.watcher.add(new VideoWatchGraph(key, counts[1]));
      this.guest.add(new VideoWatchGraph(key, counts[2]));
    }
  }

  @Data
  public static class VideoWatchGraph {
    int x;
    int y;

    public VideoWatchGraph(int x, int y) {
      this.x = x;
      this.y = y;
    }
  }
}
