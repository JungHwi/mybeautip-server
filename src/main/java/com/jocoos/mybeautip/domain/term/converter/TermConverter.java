package com.jocoos.mybeautip.domain.term.converter;

import com.jocoos.mybeautip.domain.term.dto.TermResponse;
import com.jocoos.mybeautip.domain.term.persistence.domain.Term;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TermConverter {
    List<TermResponse> convertToListResponse(List<Term> terms);
}
