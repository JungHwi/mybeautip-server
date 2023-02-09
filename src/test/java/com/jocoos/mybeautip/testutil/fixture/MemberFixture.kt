package com.jocoos.mybeautip.testutil.fixture

import com.jocoos.mybeautip.domain.member.code.InfluencerStatus
import com.jocoos.mybeautip.domain.member.code.MemberStatus
import com.jocoos.mybeautip.domain.member.code.MemberStatus.ACTIVE
import com.jocoos.mybeautip.domain.member.persistence.domain.Influencer
import com.jocoos.mybeautip.domain.member.persistence.domain.MemberActivityCount
import com.jocoos.mybeautip.domain.member.persistence.domain.MemberMemo
import com.jocoos.mybeautip.domain.member.persistence.domain.UsernameCombinationWord
import com.jocoos.mybeautip.domain.point.code.ActivityPointType
import com.jocoos.mybeautip.domain.point.code.ActivityPointType.GET_LIKE_COMMUNITY
import com.jocoos.mybeautip.member.Member
import com.jocoos.mybeautip.member.address.Address
import com.jocoos.mybeautip.member.order.Order
import com.jocoos.mybeautip.member.point.MemberPoint
import com.jocoos.mybeautip.member.point.MemberPoint.STATE_EARNED_POINT
import com.jocoos.mybeautip.member.vo.Birthday
import java.time.ZonedDateTime
import java.util.*

fun makeMembers(
    memberCount: Int,
    status: MemberStatus
): List<Member> {
    return IntRange(0, memberCount)
        .map { makeMember(status = status) }
        .toList()
}

fun makeMember(
    id: Long? = null,
    tag: String = "tag",
    status: MemberStatus = ACTIVE,
    visible: Boolean = true,
    username: String = "username",
    birthday: Birthday? = null,
    avatarFilename: String = "avatarFilename",
    email: String = "email",
    phoneNumber: String = "phoneNumber",
    point: Int = 0,
    intro: String? = null,
    link: Int = 0,
    permission: Int = 19,
    followerCount: Int = 0,
    followingCount: Int = 0,
    reportCount: Int = 0,
    publicVideoCount: Int = 0,
    totalVideoCount: Int = 0,
    revenue: Int = 0,
    revenueModifiedAt: Date? = null,
    pushable: Boolean = true,
    createdAt: Date = Date(),
    modifiedAt: Date = Date(),
    lastLoggedAt: ZonedDateTime = ZonedDateTime.now(),
    deletedAt: Date? = null,
): Member {
    return Member(
        id,
        tag,
        status,
        visible,
        username,
        birthday,
        avatarFilename,
        email,
        phoneNumber,
        point,
        intro,
        link,
        permission,
        followerCount,
        followingCount,
        reportCount,
        publicVideoCount,
        totalVideoCount,
        revenue,
        revenueModifiedAt,
        pushable,
        createdAt,
        modifiedAt,
        lastLoggedAt,
        deletedAt
    )
}

fun makeInfluencer(member: Member): Influencer {
    return Influencer(member.id, InfluencerStatus.ACTIVE, 1, ZonedDateTime.now())
}

fun makeAddress(
    id: Long = 1,
    base: Boolean = true,
    title: String = "title",
    recipient: String = "recipient",
    phone: String = "phone",
    zipNo: String = "zipNo",
    roadAddrPart1: String = "roadAddrPart1",
    roadAddrPart2: String = "roadAddrPart2",
    jibunAddr: String = "jibunAddr",
    detailAddress: String = "detailAddress",
    areaShipping: Int = 1,
): Address {
    val address = Address()
    address.apply {
        address.id = id
        address.base = base
        address.title = title
        address.recipient = recipient
        address.phone = phone
        address.zipNo = zipNo
        address.roadAddrPart1 = roadAddrPart1
        address.roadAddrPart2 = roadAddrPart2
        address.jibunAddr = jibunAddr
        address.detailAddress = detailAddress
        address.areaShipping = areaShipping
    }
    return address
}

fun makeActivityCount(
    member: Member
): MemberActivityCount {
    return MemberActivityCount(member)
}

fun makeMemberPoint(
    id: Long? = null,
    state: Int = STATE_EARNED_POINT,
    point: Int = 100,
    member: Member,
    order: Order? = null,
    eventId: Long? = null,
    activityType: ActivityPointType? = GET_LIKE_COMMUNITY,
    activityDomainId: Long? = 1,
    earnedAt: Date = Date(),
    expiryAt: Date = Date(),
    expiredAt: Date = Date(),
    remind: Boolean = false
): MemberPoint {
    return MemberPoint(
        id,
        state,
        point,
        member,
        order,
        eventId,
        activityType,
        activityDomainId,
        earnedAt,
        expiryAt,
        expiredAt,
        remind,
        "reason"
    )
}

fun makeMemberMemo(
    content: String = "content",
    target: Member,
    createdBy: Member
) : MemberMemo {
    return MemberMemo(content, target, createdBy)
}

fun makeUsernameCombinationWord(
    sequence: Int,
    word: String
) : UsernameCombinationWord {
    return UsernameCombinationWord(sequence, word)
}
