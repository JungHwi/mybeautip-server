package com.jocoos.mybeautip.domain.operation.converter;

import com.jocoos.mybeautip.domain.member.converter.MemberConverter;
import com.jocoos.mybeautip.domain.operation.dto.OperationLogRequest;
import com.jocoos.mybeautip.domain.operation.dto.OperationLogResponse;
import com.jocoos.mybeautip.domain.operation.persistence.domain.OperationLog;
import com.jocoos.mybeautip.domain.operation.service.dao.OperationLogInterface;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

@Mapper(componentModel = "spring", uses = {MemberConverter.class})
public abstract class OperationLogConverter {

    public abstract OperationLog convertsToEntity(OperationLogRequest request);

    public OperationLogRequest converts(OperationLogInterface request) {
        return OperationLogRequest.builder()
                .operationType(request.getOperationType())
                .targetType(request.getOperationType().getTargetType())
                .targetId(request.getTargetId())
                .description(request.getDescription())
                .createdBy(request.getCreatedBy())
                .build();
    }

    @Mapping(target = "adminMember", source = "createdBy")
    public abstract OperationLogResponse converts(OperationLog entity);

    public abstract List<OperationLogResponse> converts(List<OperationLog> entities);

    public PageResponse<OperationLogResponse> converts(Page<OperationLog> entities) {
        List<OperationLogResponse> responseList = this.converts(entities.getContent());
        Page<OperationLogResponse> responsePages = new PageImpl<>(responseList);

        return new PageResponse<>(responsePages);
    }
}
