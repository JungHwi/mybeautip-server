package com.jocoos.mybeautip.domain.point.converter;

import com.jocoos.mybeautip.domain.point.dto.PointHistoryResponse;
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
    PointHistoryResponse convertToResponse(MemberPoint memberPoint);

    List<PointHistoryResponse> convertToResponse(List<MemberPoint> memberPointList);
}
