package com.jocoos.mybeautip.testutil.fixture

import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType
import com.jocoos.mybeautip.domain.community.code.CommunityStatus
import com.jocoos.mybeautip.domain.community.code.CommunityStatus.NORMAL
import com.jocoos.mybeautip.domain.community.persistence.domain.Community
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityComment
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityFile
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityReport
import com.jocoos.mybeautip.domain.community.persistence.domain.vote.CommunityVote
import com.jocoos.mybeautip.domain.scrap.code.ScrapType
import com.jocoos.mybeautip.domain.scrap.code.ScrapType.COMMUNITY
import com.jocoos.mybeautip.domain.scrap.persistence.domain.Scrap
import com.jocoos.mybeautip.member.Member
import java.time.ZonedDateTime

fun makeCommunityCategory(
    id: Long? = null,
    parentId: Long? = 1,
    type: CommunityCategoryType = CommunityCategoryType.NORMAL,
    sort: Int = 1,
    title: String = "title",
    description: String = "description",
    hint: String = "hint",
    isInSummary: Boolean = false
) : CommunityCategory {
    return CommunityCategory(id, parentId, type, sort, title, description, hint, isInSummary)
}


fun makeCommunity(
    id : Long? = null,
    category: CommunityCategory? = null,
    categoryId: Long = 5,
    eventId: Long? = null,
    isWin: Boolean = false,
    member: Member? = null,
    memberId: Long = 1,
    status: CommunityStatus = NORMAL,
    title: String? = null,
    contents: String = "글 내용입니다",
    viewCount: Int = 0,
    likeCount: Int = 0,
    commentCount: Int = 0,
    reportCount: Int = 0,
    isTopFix: Boolean = false,
    sortedAt: ZonedDateTime = ZonedDateTime.now(),
    communityFileList: List<CommunityFile> = listOf(),
    communityVoteList: List<CommunityVote>?  = listOf()
): Community {
    return Community(
        id,
        category?.id ?: categoryId,
        eventId,
        isWin,
        member?.id ?: memberId,
        status,
        title,
        contents,
        viewCount,
        likeCount,
        commentCount,
        reportCount,
        isTopFix,
        sortedAt,
        communityFileList,
        communityVoteList,
        member,
        category
    )
}


fun makeCommunityComment(
    id : Long? = null,
    community: Community? = null,
    categoryId: Long = 1,
    communityId: Long = 1,
    member: Member? = null,
    memberId: Long = 1,
    parentId: Long? = null,
    status: CommunityStatus = NORMAL,
    contents: String = "contents",
    likeCount: Int = 0,
    commentCount: Int = 0,
    reportCount: Int = 0,
    file: String = "filename"
) : CommunityComment {
    return CommunityComment.builder()
        .id(id)
        .categoryId(community?.categoryId ?: categoryId)
        .communityId(community?.id ?: communityId)
        .member(member)
        .memberId(member?.id ?: memberId)
        .parentId(parentId)
        .status(status)
        .contents(contents)
        .likeCount(likeCount)
        .commentCount(commentCount)
        .reportCount(reportCount)
        .file(file)
        .build();
}

fun makeCommunityFile(
    file: String = "filename",
    community: Community
) : CommunityFile {
    return CommunityFile(file, community)
}

fun makeCommunityVote(
    community: Community,
    communityFile: CommunityFile
) : CommunityVote {
    return CommunityVote(community, communityFile)
}

fun makeCommunityReport(
    memberId: Long,
    reportedId: Long,
    communityId: Long
) : CommunityReport {
    return CommunityReport(memberId, reportedId, communityId)
}

fun makeCommunityScrap(
    memberId: Long,
    communityId: Long,
) : Scrap {
    val scrap = Scrap(memberId, COMMUNITY, communityId)
    scrap.apply { isScrap = true }
    return scrap
}
