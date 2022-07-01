package com.jocoos.mybeautip.domain.point.converter;

import com.jocoos.mybeautip.domain.point.dto.PointHistoryListResponse;
import com.jocoos.mybeautip.member.point.MemberPoint;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MemberPointConverter {

    @Mappings({
            @Mapping(target = "title", ignore = true)
    })
    PointHistoryListResponse convertToResponse(MemberPoint memberPoint);

    List<PointHistoryListResponse> convertToResponse(List<MemberPoint> memberPointList);
}
