package com.jocoos.mybeautip.domain.member.converter;

import com.jocoos.mybeautip.domain.member.code.MemberStatus;
import com.jocoos.mybeautip.domain.member.dto.*;
import com.jocoos.mybeautip.domain.member.vo.MemberBasicSearchResult;
import com.jocoos.mybeautip.domain.member.vo.MemberSearchResult;
import com.jocoos.mybeautip.member.point.MemberPoint;
import com.jocoos.mybeautip.member.report.Report;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.jocoos.mybeautip.global.util.date.ZonedDateTimeUtil.toUTCZoned;

@Mapper(componentModel = "spring")
public interface AdminMemberConverter {
    default List<MemberStatusResponse> convert(Map<MemberStatus, Long> statusCountMap) {
        return MemberStatusResponse.from(statusCountMap);
    }

    @Mapping(target = ".", source = "result")
    AdminMemberDetailResponse convert(MemberSearchResult result,
                                      Long communityCount,
                                      Long communityCommentCount,
                                      Long videoCommentCount,
                                      Long invitedFriendCount,
                                      int expiryPoint,
                                      boolean isAgreeMarketingTerm);


    default List<AdminMemberPointResponse> toPointResponse(List<MemberPoint> memberPoints) {
        return memberPoints.stream()
                .map(AdminMemberPointResponse::from)
                .toList();
    }

    List<AdminMemberReportResponse> toReportResponse(List<Report> reports);

    @Mapping(target = "accuser.id", source = "report.me.id")
    @Mapping(target = "accuser.username", source = "report.me.username")
    @Mapping(target = "reportedAt", source = "createdAt", qualifiedByName = "toUTCZoned")
    AdminMemberReportResponse toReportResponse(Report report);

    @Named("toUTCZoned")
    default ZonedDateTime toUTC(Date date) {
        return toUTCZoned(date);
    }

    List<AdminMemberResponse> toListResponse(List<MemberBasicSearchResult> content);
}
