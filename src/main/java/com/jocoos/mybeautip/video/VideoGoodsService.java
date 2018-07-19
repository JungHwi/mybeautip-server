package com.jocoos.mybeautip.video;

import com.jocoos.mybeautip.member.MemberController;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.restapi.CursorRequest;
import com.jocoos.mybeautip.restapi.Response;
import com.jocoos.mybeautip.restapi.VideoGoodsController;
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
public class VideoGoodsService {
  private final VideoGoodsRepository videoGoodsRepository;
  private final MemberRepository memberRepository;

  public VideoGoodsService(VideoGoodsRepository videoGoodsRepository,
                           MemberRepository memberRepository) {
    this.videoGoodsRepository = videoGoodsRepository;
    this.memberRepository = memberRepository;
  }

  public Response getVideos(String goodsNo, CursorRequest request, String requestUri) {
    Date startCursor = (Strings.isBlank(request.getCursor())) ?
            new Date(System.currentTimeMillis()) : new Date(Long.parseLong(request.getCursor()));

    Slice<VideoGoods> slice = videoGoodsRepository.findByGoodsNo(goodsNo, startCursor,
            of(0, request.getCount()));
    List<VideoGoodsController.VideoGoodsInfo> list = new ArrayList<>();
    VideoGoodsController.VideoGoodsInfo videoInfo;
    for (VideoGoods video : slice.getContent()) {
      videoInfo = new VideoGoodsController.VideoGoodsInfo(video);
      videoInfo.setMember(new MemberController.MemberInfo(memberRepository.getOne(video.getMemberId())));
      list.add(videoInfo);
    }

    Response<VideoGoodsController.VideoGoodsInfo> response = new Response<>();
    if (slice.getContent().size() >= request.getCount()) {
      VideoGoods videoGoods = slice.getContent().get(slice.getSize() - 1);
      String nextCursor = String.valueOf(videoGoods.getCreatedAt().getTime());
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