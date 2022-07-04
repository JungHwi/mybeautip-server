package com.jocoos.mybeautip.domain.point.converter;

import com.jocoos.mybeautip.domain.point.code.PointStatus;
import com.jocoos.mybeautip.domain.point.dto.PointHistoryResponse;
import com.jocoos.mybeautip.member.point.MemberPoint;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MemberPointConverter {

    @Mappings({
            @Mapping(target = "title", ignore = true),
            @Mapping(target = "status", ignore = true)
    })
    PointHistoryResponse convertToResponse(MemberPoint memberPoint);

    List<PointHistoryResponse> convertToResponse(List<MemberPoint> memberPointList);

    @AfterMapping
    default void convertToResponse(@MappingTarget PointHistoryResponse response, MemberPoint memberPoint) {
        response.setStatus(PointStatus.getPointStatus(memberPoint.getState()));
    }
}
