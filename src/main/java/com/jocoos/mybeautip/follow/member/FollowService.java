package com.jocoos.mybeautip.follow.member;

import com.jocoos.mybeautip.member.MemberController;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.restapi.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.domain.PageRequest.of;

@Service
@Slf4j
public class FollowService {
  
  private static FollowRepository followRepository;
  private static MemberRepository memberRepository;
  
  public FollowService(FollowRepository followRepository, MemberRepository memberRepository) {
    this.followRepository = followRepository;
    this.memberRepository = memberRepository;
  }
  
  public Response getFollowings(String requestUri, long i, String cursor, int count) {
    long startCursor;
  
    if (Strings.isBlank(cursor)) {
      startCursor = System.currentTimeMillis();
    } else {
      startCursor = Long.parseLong(cursor);
    }
  
    List<MemberController.MemberInfo> list = new ArrayList<>();
    Slice<Follow> slice = followRepository.findAllByI(i, startCursor, of(0, count));
    for (Follow follow : slice.getContent()) {
      list.add(new MemberController.MemberInfo(memberRepository.getOne(follow.getYou())));
    }
  
    Response<MemberController.MemberInfo> response = new Response();
    if (slice.getContent().size() >= count) {
      Follow follow = slice.getContent().get(slice.getSize() - 1);
      String nextCursor = String.valueOf(follow.getCreatedAt());
      String nextRef = generateNextRef(requestUri, nextCursor, count);
      response.setNextCursor(nextCursor);
      response.setNextRef(nextRef);
    } else {
      response.setNextCursor("");
      response.setNextRef("");
    }
    
    response.setContent(list);
    return response;
  }
  
  public Response getFollowers(String requestUri, long you, String cursor, int count) {
    long startCursor;
    
    if (Strings.isBlank(cursor)) {
      startCursor = System.currentTimeMillis();
    } else {
      startCursor = Long.parseLong(cursor);
    }
    
    Slice<Follow> slice = followRepository.findAllByYou(you, startCursor, of(0, count));
    List<MemberController.MemberInfo> list = new ArrayList<>();
    for (Follow follow : slice.getContent()) {
      list.add(new MemberController.MemberInfo(memberRepository.getOne(follow.getYou())));
    }
    
    Response<MemberController.MemberInfo> response = new Response();
    if (slice.getContent().size() >= count) {
      Follow follow = slice.getContent().get(slice.getSize() - 1);
      String nextCursor = String.valueOf(follow.getCreatedAt());
      String nextRef = generateNextRef(requestUri, nextCursor, count);
      response.setNextCursor(nextCursor);
      response.setNextRef(nextRef);
    } else {
      response.setNextCursor("");
      response.setNextRef("");
    }
    
    response.setContent(list);
  
    return response;
  }
  
  private String generateNextRef(String requestUri, String nextCursor, int count) {
    StringBuilder nextRef = new StringBuilder();
    nextRef.append(requestUri)
        .append("?cursor=").append(nextCursor)
        .append("&count=").append(count);
    
    return nextRef.toString();
  }
}