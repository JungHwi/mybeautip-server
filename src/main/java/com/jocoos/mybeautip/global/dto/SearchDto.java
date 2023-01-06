package com.jocoos.mybeautip.global.dto;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Pageable;

@Getter
@SuperBuilder
public class SearchDto {

    protected Pageable pageable;
}
