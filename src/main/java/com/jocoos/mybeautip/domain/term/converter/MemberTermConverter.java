package com.jocoos.mybeautip.domain.term.converter;

import com.jocoos.mybeautip.domain.term.dto.MemberTermRequest;
import com.jocoos.mybeautip.domain.term.dto.MemberTermResponse;
import com.jocoos.mybeautip.domain.term.persistence.domain.MemberTerm;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MemberTermConverter {

    @Mapping(source = "termId", target = "term", qualifiedByName = "convert")
    List<MemberTerm> convertFrom(List<MemberTermRequest> requests);

    @Mapping(source = "term.id", target = "termId")
    @Mapping(source = "member.id", target = "memberId")
    MemberTermResponse convertToResponse(MemberTerm memberTerm);
    List<MemberTermResponse> convertToListResponse(List<MemberTerm> memberTerms);
}
