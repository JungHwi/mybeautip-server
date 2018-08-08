package com.jocoos.mybeautip.video;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import static org.springframework.data.domain.PageRequest.of;

import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.restapi.CursorRequest;
import com.jocoos.mybeautip.restapi.Response;

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
    List<VideoGoodsInfo> list = new ArrayList<>();
    VideoGoodsInfo videoInfo;
    for (VideoGoods video : slice.getContent()) {
      videoInfo = new VideoGoodsInfo(video,
        new MemberInfo(memberRepository.getOne(video.getMemberId())));
      list.add(videoInfo);
    }

    Response<VideoGoodsInfo> response = new Response<>();
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