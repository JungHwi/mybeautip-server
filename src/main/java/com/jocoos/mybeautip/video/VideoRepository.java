package com.jocoos.mybeautip.video;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jocoos.mybeautip.member.Member;

public interface VideoRepository extends CrudRepository<Video, Long> {

  // Get public videos
  @Query("select v from Video v where v.visibility = 'PUBLIC' and v.createdAt < ?1 and v.deletedAt is null order by v.createdAt desc")
  Slice<Video> getAnyoneAllVideos(Date cursor, Pageable pageable);

  @Query("select v from Video v where v.visibility = 'PUBLIC' and v.type = 'BROADCASTED' and v.createdAt < ?1 and v.deletedAt is null order by v.createdAt desc")
  Slice<Video> getAnyoneLiveAndVodVideos(Date cursor, Pageable pageable);

  @Query("select v from Video v where v.visibility = 'PUBLIC' and v.type = 'UPLOADED' and v.createdAt < ?1 and v.deletedAt is null order by v.createdAt desc")
  Slice<Video> getAnyoneMotdVideos(Date cursor, Pageable pageable);

  @Query("select v from Video v where v.visibility = 'PUBLIC' and v.type = 'BROADCASTED' and v.state = 'LIVE' and v.createdAt < ?1 and v.deletedAt is null order by v.createdAt desc")
  Slice<Video> getAnyoneLiveVideos(Date cursor, Pageable pageable);

  @Query("select v from Video v where v.visibility = 'PUBLIC' and v.type = 'BROADCASTED' and v.state = 'VOD' and v.createdAt < ?1 and v.deletedAt is null order by v.createdAt desc")
  Slice<Video> getAnyoneVodVideos(Date cursor, Pageable pageable);

  @Query("select v from Video v where v.visibility = 'PUBLIC' and v.state = 'VOD' and v.createdAt < ?1 and v.deletedAt is null order by v.createdAt desc")
  Slice<Video> getAnyoneVodAndMotdVideos(Date cursor, Pageable pageable);

  // Get My Videos
  @Query("select v from Video v where v.member = ?1 and v.createdAt < ?2 and v.deletedAt is null order by v.createdAt desc")
  Slice<Video> getMyAllVideos(Member me, Date cursor, Pageable pageable);

  @Query("select v from Video v where v.member = ?1 and v.type = 'BROADCASTED' and v.createdAt < ?2 and v.deletedAt is null order by v.createdAt desc")
  Slice<Video> getMyLiveAndVodVideos(Member me, Date cursor, Pageable pageable);

  @Query("select v from Video v where v.member = ?1 and v.type = 'UPLOADED' and v.createdAt < ?2 and v.deletedAt is null order by v.createdAt desc")
  Slice<Video> getMyMotdVideos(Member me, Date cursor, Pageable pageable);

  @Query("select v from Video v where v.member = ?1 and v.type = 'BROADCASTED' and v.state = 'LIVE' and v.createdAt < ?2 and v.deletedAt is null order by v.createdAt desc")
  Slice<Video> getMyLiveVideos(Member me, Date cursor, Pageable pageable);

  @Query("select v from Video v where v.member = ?1 and v.type = 'BROADCASTED' and v.state = 'VOD' and v.createdAt < ?2 and v.deletedAt is null order by v.createdAt desc")
  Slice<Video> getMyVodVideos(Member me, Date cursor, Pageable pageable);

  @Query("select v from Video v where v.member = ?1 and v.state = 'VOD' and v.createdAt < ?2 and v.deletedAt is null order by v.createdAt desc")
  Slice<Video> getMyVodAndMotdVideos(Member me, Date cursor, Pageable pageable);

  // Get Member's videos
  @Query("select v from Video v where v.member = ?1 and v.visibility = 'PUBLIC' and v.createdAt < ?2 and v.deletedAt is null order by v.createdAt desc")
  Slice<Video> getUserAllVideos(Member member, Date cursor, Pageable pageable);

  @Query("select v from Video v where v.member = ?1 and v.visibility = 'PUBLIC' and v.type = 'BROADCASTED' and v.createdAt < ?2 and v.deletedAt is null order by v.createdAt desc")
  Slice<Video> getUserLiveAndVodVideos(Member member, Date cursor, Pageable pageable);

  @Query("select v from Video v where v.member = ?1 and v.visibility = 'PUBLIC' and v.type = 'UPLOADED' and v.createdAt < ?2 and v.deletedAt is null order by v.createdAt desc")
  Slice<Video> getUserMotdVideos(Member member, Date cursor, Pageable pageable);

  @Query("select v from Video v where v.member = ?1 and v.visibility = 'PUBLIC' and v.type = 'BROADCASTED' and v.state = 'LIVE' and v.createdAt < ?2 and v.deletedAt is null order by v.createdAt desc")
  Slice<Video> getUserLiveVideos(Member member, Date cursor, Pageable pageable);

  @Query("select v from Video v where v.member = ?1 and v.visibility = 'PUBLIC' and v.type = 'BROADCASTED' and v.state = 'VOD' and v.createdAt < ?2 and v.deletedAt is null order by v.createdAt desc")
  Slice<Video> getUserVodVideos(Member member, Date cursor, Pageable pageable);

  @Query("select v from Video v where v.member = ?1 and v.visibility = 'PUBLIC' and v.state = 'VOD' and v.createdAt < ?2 and v.deletedAt is null order by v.createdAt desc")
  Slice<Video> getUserVodAndMotdVideos(Member member, Date cursor, Pageable pageable);

  @Query("select v from Video v where v.visibility = 'PUBLIC' and v.deletedAt is null " +
    "and (v.title like concat('%',:keyword,'%') or v.content like concat('%',:keyword,'%') " +
    "or v.member.username like concat('%',:keyword,'%') or v.member.intro like concat('%',:keyword,'%')) " +
    "and v.createdAt < :cursor order by v.createdAt desc")
  Slice<Video> searchVideos(@Param("keyword") String keyword, @Param("cursor") Date cursor, Pageable pageable);

  Optional<Video> findByIdAndDeletedAtIsNull(Long id);

  Optional<Video> findByVideoKeyAndDeletedAtIsNull(String videoKey);

  Optional<Video> findByVideoKey(String videoKey);

  Optional<Video> findByIdAndMemberId(Long id, Long memberId);

  @Modifying
  @Query("update Video v set v.commentCount = v.commentCount + ?2, v.modifiedAt = now() where v.id = ?1")
  void updateCommentCount(Long id, int count);

  @Modifying
  @Query("update Video v set v.likeCount = v.likeCount + ?2, v.modifiedAt = now() where v.id = ?1")
  void updateLikeCount(Long id, int i);

  @Modifying
  @Query("update Video v set v.heartCount = v.heartCount + ?2, v.modifiedAt = now() where v.id = ?1")
  void updateHeartCount(Long id, int i);

  @Modifying
  @Query("update Video v set v.viewCount = v.viewCount + ?2, v.modifiedAt = now() where v.id = ?1")
  void updateViewCount(Long id, int i);

  @Modifying
  @Query("update Video v set v.totalWatchCount = v.totalWatchCount + ?2, v.modifiedAt = now() where v.id = ?1")
  void updateTotalWatchCount(Long id, int i);
}