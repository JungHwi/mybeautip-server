package com.jocoos.mybeautip.member;

import javax.transaction.Transactional;

import com.jocoos.mybeautip.member.cart.CartRepository;
import com.jocoos.mybeautip.member.following.FollowingRepository;
import com.jocoos.mybeautip.video.VideoRepository;
import com.jocoos.mybeautip.video.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PostProcessService {
  
  private final VideoService videoService;
  private final VideoRepository videoRepository;
  private final FollowingRepository followingRepository;
  private final CartRepository cartRepository;
  
  public PostProcessService(VideoService videoService,
                            VideoRepository videoRepository,
                            FollowingRepository followingRepository,
                            CartRepository cartRepository) {
    this.videoService = videoService;
    this.videoRepository = videoRepository;
    this.followingRepository = followingRepository;
    this.cartRepository = cartRepository;
  }
  
  @Async
  @Transactional
  public void deleteMember(Member member) {
    // 1. Delete member's all videos (using videoService)
    // 2. Delete all followings related members (followings/followers)
    // 3. Delete all cart items created by member
    // TODO: Delete garbage data depends on policy(order, addresses, account, likes, views, blocks, reports, comments)

    log.debug("Member {} deleted: video will be deleted", member.getId());
    videoRepository.findByMemberAndDeletedAtIsNull(member)
        .forEach(video -> {
          videoService.deleteVideo(member.getId(), video.getVideoKey());
          log.debug("{} video deleted", video.getId());
        });

    log.debug("Member {} deleted: followings will be deleted", member.getId());
    followingRepository.findByMemberMeIdOrMemberYouId(member.getId(), member.getId())
        .forEach(followingRepository::delete);

    log.debug("Member {} deleted: cart items will be deleted", member.getId());
    cartRepository.findByCreatedById(member.getId())
        .forEach(cartRepository::delete);
  }
}