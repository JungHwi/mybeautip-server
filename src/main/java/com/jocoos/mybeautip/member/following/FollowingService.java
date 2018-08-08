package com.jocoos.mybeautip.member.following;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.restapi.CursorResponse;

@Service
@Slf4j
public class FollowingService {

  private final MemberService memberService;
  private final FollowingRepository followingRepository;

  public FollowingService(MemberService memberService,
                          FollowingRepository followingRepository) {
    this.memberService = memberService;
    this.followingRepository = followingRepository;
  }
  
  CursorResponse getFollowings(String requestUri, long me, String cursor, int count) {
    Date startCursor = (Strings.isBlank(cursor)) ?
      new Date(System.currentTimeMillis()) : new Date(Long.parseLong(cursor));

    PageRequest pageable = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "id"));
    Slice<Following> slice = followingRepository.findByCreatedAtBeforeAndMemberMeId(startCursor, me, pageable);
    
    List<FollowingInfo> result = new ArrayList<>();
    FollowingInfo followingInfo;

    for (Following following : slice.getContent()) {
      followingInfo = new FollowingInfo(following, new MemberInfo(following.getMemberYou()));

      // Add following id when I follow
      Optional<Following> optional = followingRepository.findByMemberMeIdAndMemberYouId(
        memberService.currentMemberId(), following.getMemberYou().getId());
      if (optional.isPresent()) {
        followingInfo.setFollowingId(optional.get().getId());
      }
      result.add(followingInfo);
    }

    if (result.size() > 0) {
      String nextCursor = String.valueOf(result.get(result.size() - 1).getCreatedAt());
      return new CursorResponse.Builder<>(requestUri, result)
        .withCount(count)
        .withCursor(nextCursor).toBuild();
    } else {
      return new CursorResponse.Builder<>(requestUri, result).toBuild();
    }
  }
  
  CursorResponse getFollowers(String requestUri, long you, String cursor, int count) {
    Date startCursor = (Strings.isBlank(cursor)) ?
        new Date(System.currentTimeMillis()) : new Date(Long.parseLong(cursor));

    PageRequest pageable = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "id"));
    Slice<Following> slice = followingRepository.findByCreatedAtBeforeAndMemberYouId(startCursor, you, pageable);

    List<FollowingInfo> result = new ArrayList<>();
    FollowingInfo followingInfo;
    
    for (Following follower : slice.getContent()) {
      followingInfo = new FollowingInfo(follower, new MemberInfo(follower.getMemberMe()));

      // Add following id when I follow
      Optional<Following> optional = followingRepository.findByMemberMeIdAndMemberYouId(
        memberService.currentMemberId(), follower.getMemberMe().getId());
      if (optional.isPresent()) {
        followingInfo.setFollowingId(optional.get().getId());
      }
      result.add(followingInfo);
    }

    if (result.size() > 0) {
      String nextCursor = String.valueOf(result.get(result.size() - 1).getCreatedAt());
      return new CursorResponse.Builder<>(requestUri, result)
        .withCount(count)
        .withCursor(nextCursor).toBuild();
    } else {
      return new CursorResponse.Builder<>(requestUri, result).toBuild();
    }
  }

  @Data
  @NoArgsConstructor
  public class FollowingInfo {
    public Long followingId;
    public Long createdAt;
    public MemberInfo member;

    FollowingInfo(Following following, MemberInfo member) {
      this.createdAt = following.getCreatedAt().getTime();
      this.member = member;
    }
  }
}