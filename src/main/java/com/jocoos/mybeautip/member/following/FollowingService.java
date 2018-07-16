package com.jocoos.mybeautip.member.following;

import com.jocoos.mybeautip.member.MemberController;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.restapi.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.data.domain.PageRequest.of;

@Service
@Slf4j
public class FollowingService {
  private static FollowingRepository followingRepository;
  private static MemberRepository memberRepository;
  
  public FollowingService(FollowingRepository followingRepository, MemberRepository memberRepository) {
    this.followingRepository = followingRepository;
    this.memberRepository = memberRepository;
  }
  
  public Response getFollowings(String requestUri, long me, String cursor, int count) {
    Date startCursor = (Strings.isBlank(cursor)) ?
        new Date(System.currentTimeMillis()) : new Date(Long.parseLong(cursor));
  
    Slice<Following> slice = followingRepository.findAllByMe(me, startCursor, of(0, count));
    
    List<FollowingInfo> list = new ArrayList<>();
    FollowingInfo followingInfo;
    for (Following following : slice.getContent()) {
      followingInfo = new FollowingInfo(following);
      followingInfo.setMember(new MemberController.MemberInfo(memberRepository.getOne(following.getYou())));
      list.add(followingInfo);
    }
  
    Response<FollowingInfo> response = new Response<>();
    if (slice.getContent().size() >= count) {
      Following following = slice.getContent().get(slice.getSize() - 1);
      String nextCursor = String.valueOf(following.getCreatedAt().getTime());
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
    Date startCursor = (Strings.isBlank(cursor)) ?
        new Date(System.currentTimeMillis()) : new Date(Long.parseLong(cursor));
    
    Slice<Following> slice = followingRepository.findAllByYou(you, startCursor, of(0, count));
    List<FollowingInfo> list = new ArrayList<>();
    FollowingInfo followingInfo;
    
    for (Following following : slice.getContent()) {
      followingInfo = new FollowingInfo(following);
      followingInfo.setMember(new MemberController.MemberInfo(memberRepository.getOne(following.getYou())));
      list.add(followingInfo);
    }
    
    Response<FollowingInfo> response = new Response<>();
    if (slice.getContent().size() >= count) {
      Following following = slice.getContent().get(slice.getSize() - 1);
      String nextCursor = String.valueOf(following.getCreatedAt().getTime());
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