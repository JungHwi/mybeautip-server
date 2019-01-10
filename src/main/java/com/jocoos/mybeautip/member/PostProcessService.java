package com.jocoos.mybeautip.member;

import javax.transaction.Transactional;

import com.jocoos.mybeautip.member.cart.CartRepository;
import com.jocoos.mybeautip.member.following.FollowingRepository;
import com.jocoos.mybeautip.notification.NotificationRepository;
import com.jocoos.mybeautip.recommendation.KeywordRecommendationRepository;
import com.jocoos.mybeautip.recommendation.MemberRecommendationRepository;
import com.jocoos.mybeautip.video.VideoGoodsRepository;
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
  private final MemberRepository memberRepository;
  private final MemberRecommendationRepository memberRecommendationRepository;
  private final VideoGoodsRepository videoGoodsRepository;
  private final NotificationRepository notificationRepository;
  private final KeywordRecommendationRepository keywordRecommendationRepository;
  
  public PostProcessService(VideoService videoService,
                            VideoRepository videoRepository,
                            FollowingRepository followingRepository,
                            CartRepository cartRepository,
                            MemberRepository memberRepository,
                            MemberRecommendationRepository memberRecommendationRepository,
                            VideoGoodsRepository videoGoodsRepository,
                            NotificationRepository notificationRepository,
                            KeywordRecommendationRepository keywordRecommendationRepository) {
    this.videoService = videoService;
    this.videoRepository = videoRepository;
    this.followingRepository = followingRepository;
    this.cartRepository = cartRepository;
    this.memberRepository = memberRepository;
    this.memberRecommendationRepository = memberRecommendationRepository;
    this.videoGoodsRepository = videoGoodsRepository;
    this.notificationRepository = notificationRepository;
    this.keywordRecommendationRepository = keywordRecommendationRepository;
  }
  
  @Async
  @Transactional
  public void deleteMember(Member member) {
    // 1. Delete member's all videos (using videoService)
    // 2. Delete all followings related members (followings/followers)
    // 3. Delete all cart items created by member
    // TODO: Delete garbage data depends on policy(order, addresses, account, likes, views, blocks, reports, comments)

    log.debug("Member {} deleted: video will be deleted", member.getId());
    videoService.deleteVideos(member);

    log.debug("Member {} deleted: followings will be deleted", member.getId());
    followingRepository.findByMemberMeId(member.getId())
        .forEach(following -> {
          followingRepository.delete(following);
          memberRepository.updateFollowerCount(following.getMemberYou().getId(), -1);
        });

    followingRepository.findByMemberYouId(member.getId())
        .forEach(following -> {
          followingRepository.delete(following);
          memberRepository.updateFollowingCount(following.getMemberMe().getId(), -1);
        });

    log.debug("Member {} deleted: cart items will be deleted", member.getId());
    cartRepository.findByCreatedById(member.getId())
        .forEach(cartRepository::delete);

    log.debug("Member {} deleted: recommended member will be deleted", member.getId());
    memberRecommendationRepository.findByMemberId(member.getId())
        .ifPresent(memberRecommendationRepository::delete);
  
    log.debug("Member {} deleted: videoGoods will be deleted", member.getId());
    videoGoodsRepository.findAllByMemberId(member.getId())
        .forEach(videoGoodsRepository::delete);
  
    log.debug("Member {} deleted: notifications will be deleted", member.getId());
    notificationRepository.findByResourceOwnerId(member.getId())
        .forEach(notificationRepository::delete);
    notificationRepository.findByTargetMemberId(member.getId())
        .forEach(notificationRepository::delete);
  
    log.debug("Member {} deleted: recommended keyword will be deleted", member.getId());
    keywordRecommendationRepository.findByMember(member)
        .ifPresent(keywordRecommendationRepository::delete);
  }
}