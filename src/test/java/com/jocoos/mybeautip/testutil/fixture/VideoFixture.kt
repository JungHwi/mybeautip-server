package com.jocoos.mybeautip.testutil.fixture

import com.jocoos.mybeautip.domain.video.code.VideoCategoryType
import com.jocoos.mybeautip.domain.video.code.VideoCategoryType.NORMAL
import com.jocoos.mybeautip.domain.video.code.VideoMaskType
import com.jocoos.mybeautip.domain.video.code.VideoMaskType.HEART
import com.jocoos.mybeautip.domain.video.code.VideoStatus
import com.jocoos.mybeautip.domain.video.code.VideoStatus.OPEN
import com.jocoos.mybeautip.domain.video.persistence.domain.VideoCategory
import com.jocoos.mybeautip.member.Member
import com.jocoos.mybeautip.video.Video
import com.jocoos.mybeautip.video.VideoCategoryMapping
import com.jocoos.mybeautip.video.Visibility
import java.util.Date

fun makeVideoCategory(
    id: Int? = null,
    parentId: Int? = 1,
    type: VideoCategoryType = NORMAL,
    sort: Int = 1,
    title: String = "title",
    description: String = "description",
    shapeFile: String = "shapeFile",
    maskType: VideoMaskType = HEART,
    categoryMapping: List<VideoCategoryMapping> = emptyList()
): VideoCategory {
    return VideoCategory(id, parentId, type, sort, title, description, shapeFile, maskType, categoryMapping)
}

fun makeVideos(
    videoNumber: Int,
    category: VideoCategory,
    member: Member
) : List<Video> {
    return IntRange(0, videoNumber - 1)
        .map { makeVideo(member = member, category = category) }
        .toList()
}

fun makeVideo(
    member: Member,
    category: VideoCategory,
    startedAt: Date = Date(),
    endedAt: Date = Date(),
    createdAt: Date = Date(),
    modifiedAt: Date = Date(),
    deletedAt: Date? = null,
    id: Long? = null,
    videoKey: String = "videoKey",
    type: String = "UPLOADED",
    state: String = "VOD",
    locked: Boolean = false,
    muted: Boolean = false,
    visibility: String = Visibility.PUBLIC.name,
    categoryMapping: List<VideoCategoryMapping> = mutableListOf(),
    title: String = "title",
    content: String = "content",
    url: String = "url",
    originalFilename: String = "file",
    thumbnailPath: String = "thumbnailPath",
    thumbnailUrl: String = "thumbnailUrl",
    chatRoomId: String? = null,
    duration: Int = 10,
    liveKey: String? = null,
    outputType: String? = null,
    data: String? = "",
    watchCount: Int = 0,
    totalWatchCount: Int = 0,
    heartCount: Int = 0,
    viewCount: Int = 0,
    likeCount: Int = 0,
    scrapCount: Int = 0,
    commentCount: Int = 0,
    orderCount: Int = 0,
    reportCount: Long = 0,
    relatedGoodsCount: Int = 0,
    relatedGoodsThumbnailUrl: String? = null,
    sorting: Int? = null,
    isTopFix: Boolean = false,
    isRecommended: Boolean = false,
    status: VideoStatus = OPEN
): Video {
    val video = Video(
        startedAt,
        endedAt,
        createdAt,
        modifiedAt,
        deletedAt,
        id,
        videoKey,
        type,
        state,
        locked,
        muted,
        visibility,
        categoryMapping,
        title,
        content,
        url,
        originalFilename,
        thumbnailPath,
        thumbnailUrl,
        chatRoomId,
        duration,
        liveKey,
        outputType,
        data,
        watchCount,
        totalWatchCount,
        heartCount,
        viewCount,
        likeCount,
        scrapCount,
        commentCount,
        orderCount,
        reportCount,
        relatedGoodsCount,
        relatedGoodsThumbnailUrl,
        member,
        sorting,
        isTopFix,
        isRecommended,
        status,
        false
    )
    video.categoryMapping = listOf(makeVideoCategoryMapping(video, category))
    return video
}

fun makeVideoCategoryMapping(
    video: Video,
    videoCategory: VideoCategory
): VideoCategoryMapping {
    return VideoCategoryMapping(video, videoCategory)
}

fun makeVideoWithData(
    member: Member,
    category: VideoCategory,
    data: String,
    startedAt: Date = Date(),
    endedAt: Date = Date(),
    createdAt: Date = Date(),
    modifiedAt: Date = Date(),
    deletedAt: Date? = null,
    id: Long? = null,
    videoKey: String = "videoKey",
    type: String = "UPLOADED",
    state: String = "VOD",
    locked: Boolean = false,
    muted: Boolean = false,
    visibility: String = Visibility.PUBLIC.name,
    categoryMapping: List<VideoCategoryMapping> = mutableListOf(),
    title: String = "title",
    content: String = "content",
    url: String = "url",
    originalFilename: String = "file",
    thumbnailPath: String = "thumbnailPath",
    thumbnailUrl: String = "thumbnailUrl",
    chatRoomId: String? = null,
    duration: Int = 10,
    liveKey: String? = null,
    outputType: String? = null,
    watchCount: Int = 0,
    totalWatchCount: Int = 0,
    heartCount: Int = 0,
    viewCount: Int = 0,
    likeCount: Int = 0,
    scrapCount: Int = 0,
    commentCount: Int = 0,
    orderCount: Int = 0,
    reportCount: Long = 0,
    relatedGoodsCount: Int = 0,
    relatedGoodsThumbnailUrl: String? = null,
    sorting: Int? = null,
    isTopFix: Boolean = false,
    isRecommended: Boolean = false,
    status: VideoStatus = OPEN
): Video {
    val video = Video(
        startedAt,
        endedAt,
        createdAt,
        modifiedAt,
        deletedAt,
        id,
        videoKey,
        type,
        state,
        locked,
        muted,
        visibility,
        categoryMapping,
        title,
        content,
        url,
        originalFilename,
        thumbnailPath,
        thumbnailUrl,
        chatRoomId,
        duration,
        liveKey,
        outputType,
        data,
        watchCount,
        totalWatchCount,
        heartCount,
        viewCount,
        likeCount,
        scrapCount,
        commentCount,
        orderCount,
        reportCount,
        relatedGoodsCount,
        relatedGoodsThumbnailUrl,
        member,
        sorting,
        isTopFix,
        isRecommended,
        status,
        false
    )
    video.categoryMapping = listOf(makeVideoCategoryMapping(video, category))
    return video
}

