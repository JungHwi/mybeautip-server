package com.jocoos.mybeautip.video;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.video.code.VideoStatus;
import com.jocoos.mybeautip.domain.video.persistence.repository.VideoCustomRepository;
import com.jocoos.mybeautip.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface VideoRepository extends ExtendedQuerydslJpaRepository<Video, Long>, VideoCustomRepository {

    // Get public videos
    @Query("select v from Video v where v.visibility = 'PUBLIC' and (v.state = 'LIVE' or v.state = 'VOD') and v.createdAt < ?1 and v.deletedAt is null order by v.createdAt desc")
    Slice<Video> getAnyoneAllVideos(Date cursor, Pageable pageable);
    @Query("select v from Video v inner join fetch VideoCategoryMapping c on v.id = c.video.id where c.videoCategory.id = ?1 and v.visibility = 'PUBLIC' and (v.state = 'LIVE' or v.state = 'VOD') and v.createdAt < ?2 and v.deletedAt is null order by v.createdAt desc")
    Slice<Video> getCategoryVideo(Integer categoryId, Date cursor, Pageable pageable);

    @Query("select v from Video v where v.visibility = 'PUBLIC' and v.type = 'BROADCASTED' and (v.state = 'LIVE' or v.state = 'VOD') and v.createdAt < ?1 and v.deletedAt is null order by v.createdAt desc")
    Slice<Video> getAnyoneLiveAndVodVideos(Date cursor, Pageable pageable);

    @Query("select v from Video v where v.visibility = 'PUBLIC' and v.type = 'UPLOADED' and (v.state = 'LIVE' or v.state = 'VOD') and v.createdAt < ?1 and v.deletedAt is null order by v.createdAt desc")
    Slice<Video> getAnyoneMotdVideos(Date cursor, Pageable pageable);

    @Query("select v from Video v where v.visibility = 'PUBLIC' and v.type = 'BROADCASTED' and v.state = 'LIVE' and v.createdAt < ?1 and v.deletedAt is null order by v.createdAt desc")
    Slice<Video> getAnyoneLiveVideos(Date cursor, Pageable pageable);

    @Query("select v from Video v where v.visibility = 'PUBLIC' and v.type = 'BROADCASTED' and v.state = 'VOD' and v.createdAt < ?1 and v.deletedAt is null order by v.createdAt desc")
    Slice<Video> getAnyoneVodVideos(Date cursor, Pageable pageable);

    @Query("select v from Video v where v.visibility = 'PUBLIC' and v.state = 'VOD' and v.createdAt < ?1 and v.deletedAt is null order by v.createdAt desc")
    Slice<Video> getAnyoneVodAndMotdVideos(Date cursor, Pageable pageable);

    // sort
    @Query("select v from Video v where v.visibility = 'PUBLIC' and v.locked = 0 and v.state = 'VOD' and v.viewCount < ?1 and v.createdAt > ?2 and v.createdAt < ?3 and v.deletedAt is null order by v.viewCount desc")
    Slice<Video> getAnyoneAllVideosOrderByViewCount(int cursor, Date fromDate, Date toDate, Pageable pageable);

    @Query("select v from Video v where v.visibility = 'PUBLIC' and v.locked = 0 and v.state = 'VOD' and v.likeCount < ?1 and v.createdAt > ?2 and v.createdAt < ?3 and v.deletedAt is null order by v.likeCount desc")
    Slice<Video> getAnyoneAllVideosOrderByLikeCount(int cursor, Date fromDate, Date toDate, Pageable pageable);

    // Get My Videos
    @Query("select v from Video v where v.member = ?1 and (v.state = 'LIVE' or v.state = 'VOD') and v.createdAt < ?2 and v.deletedAt is null order by v.createdAt desc")
    Slice<Video> getMyAllVideos(Member me, Date cursor, Pageable pageable);

    @Query("select v from Video v where v.member = ?1 and v.type = 'BROADCASTED' and v.state = 'LIVE' and v.createdAt < ?2 and v.deletedAt is null order by v.createdAt desc")
    Slice<Video> getMyLiveAndVodVideos(Member me, Date cursor, Pageable pageable);

    @Query("select v from Video v where v.member = ?1 and v.type = 'UPLOADED' and v.state = 'VOD' and v.createdAt < ?2 and v.deletedAt is null order by v.createdAt desc")
    Slice<Video> getMyMotdVideos(Member me, Date cursor, Pageable pageable);

    @Query("select v from Video v where v.member = ?1 and v.type = 'BROADCASTED' and v.state = 'LIVE' and v.createdAt < ?2 and v.deletedAt is null order by v.createdAt desc")
    Slice<Video> getMyLiveVideos(Member me, Date cursor, Pageable pageable);

    @Query("select v from Video v where v.member = ?1 and v.type = 'BROADCASTED' and v.state = 'VOD' and v.createdAt < ?2 and v.deletedAt is null order by v.createdAt desc")
    Slice<Video> getMyVodVideos(Member me, Date cursor, Pageable pageable);

    @Query("select v from Video v where v.member = ?1 and v.state = 'VOD' and v.createdAt < ?2 and v.deletedAt is null order by v.createdAt desc")
    Slice<Video> getMyVodAndMotdVideos(Member me, Date cursor, Pageable pageable);

    // Get Member's videos
    @Query("select v from Video v where v.member = ?1 and v.visibility = 'PUBLIC' and (v.state = 'LIVE' or v.state = 'VOD') and v.createdAt < ?2 and v.deletedAt is null order by v.createdAt desc")
    Slice<Video> getUserAllVideos(Member member, Date cursor, Pageable pageable);

    @Query("select v from Video v where v.member = ?1 and v.visibility = 'PUBLIC' and v.type = 'BROADCASTED' and (v.state = 'LIVE' or v.state = 'VOD') and v.createdAt < ?2 and v.deletedAt is null order by v.createdAt desc")
    Slice<Video> getUserLiveAndVodVideos(Member member, Date cursor, Pageable pageable);

    @Query("select v from Video v where v.member = ?1 and v.visibility = 'PUBLIC' and v.type = 'UPLOADED' and (v.state = 'LIVE' or v.state = 'VOD') and v.createdAt < ?2 and v.deletedAt is null order by v.createdAt desc")
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
            "and (v.state = 'LIVE' or v.state = 'VOD') and v.createdAt < :cursor order by v.createdAt desc")
    Slice<Video> searchVideos(@Param("keyword") String keyword, @Param("cursor") Date cursor, Pageable pageable);

    @Query("select v from Video v where v.visibility = 'PUBLIC' and v.deletedAt is null " +
            "and v.content like concat('%',:keyword,'%') " +
            "and (v.state = 'LIVE' or v.state = 'VOD') and v.createdAt < :cursor order by v.createdAt desc")
    Slice<Video> searchVideosWithTag(@Param("keyword") String keyword, @Param("cursor") Date cursor, Pageable pageable);

    Optional<Video> findByIdAndDeletedAtIsNull(Long id);

    Optional<Video> findByVideoKey(String videoKey);

    Optional<Video> findByVideoKeyAndDeletedAtIsNull(String videoKey);

    Optional<Video> findByIdAndMemberId(Long id, Long memberId);

    @Modifying
    @Query("update Video v set v.commentCount = v.commentCount + ?2, v.modifiedAt = now() where v.id = ?1")
    void updateCommentCount(Long id, int count);

    @Modifying
    @Query("update Video v set v.commentCount = :count, v.modifiedAt = now() where v.id = :videoId")
    void setCommentCount(@Param("videoId") Long videoId, @Param("count") int count);

    @Modifying
    @Query("update Video v set v.likeCount = v.likeCount + ?2, v.modifiedAt = now() where v.id = ?1")
    void updateLikeCount(Long id, int i);

    @Modifying
    @Query("update Video v set v.heartCount = v.heartCount + ?2, v.modifiedAt = now() where v.id = ?1")
    void updateHeartCount(Long id, int i);

    @Modifying
    @Query("update Video v set v.orderCount = v.orderCount + ?2, v.modifiedAt = now() where v.id = ?1")
    void updateOrderCount(Long id, int i);

    List<Video> findByMemberAndDeletedAtIsNull(Member member);

    Page<Video> findByTypeAndStateInAndDeletedAtIsNull(String type, Collection<String> state, Pageable pageable);

    Page<Video> findByTypeAndStateInAndDeletedAtIsNotNull(String type, Collection<String> state, Pageable pageable);

    Page<Video> findByMemberIdAndTypeAndStateInAndDeletedAtIsNull(Long owner, String type, Collection<String> states, Pageable pageable);

    Page<Video> findByMemberIdAndTypeAndStateInAndDeletedAtIsNotNull(Long owner, String type, Collection<String> states, Pageable pageable);

    Page<Video> findByTypeAndStateInAndAndCreatedAtBetweenAndDeletedAtIsNull(String type, Collection<String> state, Date from, Date now, Pageable pageable);

    Optional<Video> findTopByMemberIdAndCreatedAtBetweenAndDeletedAtIsNullOrderByCreatedAtDesc(Long owner, Date from, Date to);
    List<Video> findByIdInAndVisibility(List<Long> ids, String visibility);
    List<Video> findByStatusAndStartedAtLessThanEqualAndDeletedAtIsNull(VideoStatus status, Date startedAt);
}
