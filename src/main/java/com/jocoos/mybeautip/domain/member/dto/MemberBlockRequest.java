package com.jocoos.mybeautip.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberBlockRequest {

    @NotNull(message = "target_id can't be null")
    private Long targetId;

    @NotNull(message = "is_block can't be null")
    private Boolean isBlock;
}
