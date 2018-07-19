package com.jocoos.mybeautip.video;

import com.jocoos.mybeautip.member.MemberController;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.restapi.CursorRequest;
import com.jocoos.mybeautip.restapi.Response;
import com.jocoos.mybeautip.restapi.VideoController;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.data.domain.PageRequest.of;

@Slf4j
@Service
public class VideoService {
  private final VideoRepository videoRepository;
  private final MemberRepository memberRepository;

  public VideoService(VideoRepository videoRepository, MemberRepository memberRepository) {
    this.videoRepository = videoRepository;
    this.memberRepository = memberRepository;
  }

  public Response getVideos(String goodsNo, CursorRequest request, String requestUri) {
    Date startCursor = (Strings.isBlank(request.getCursor())) ?
            new Date(System.currentTimeMillis()) : new Date(Long.parseLong(request.getCursor()));

    Slice<Video> slice = videoRepository.findByGoodsNo(goodsNo, startCursor, of(0, request.getCount()));
    List<VideoController.VideoInfo> list = new ArrayList<>();
    VideoController.VideoInfo videoInfo;
    for (Video video : slice.getContent()) {
      videoInfo = new VideoController.VideoInfo(video);
      videoInfo.setMember(new MemberController.MemberInfo(memberRepository.getOne(video.getMemberId())));
      list.add(videoInfo);
    }

    Response<VideoController.VideoInfo> response = new Response<>();
    if (slice.getContent().size() >= request.getCount()) {
      Video video = slice.getContent().get(slice.getSize() - 1);
      String nextCursor = String.valueOf(video.getCreatedAt().getTime());
      String nextRef = response.generateNextRef(requestUri, nextCursor, request.getCount());
      response.setNextCursor(nextCursor);
      response.setNextRef(nextRef);
    } else {
      response.setNextCursor("");
      response.setNextRef("");
    }

    response.setContent(list);
    return response;
  }
}