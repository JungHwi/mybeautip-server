package com.jocoos.mybeautip.domain.member.dto;

import javax.validation.constraints.NotBlank;

public record MemoRequest(@NotBlank String memo) {
}
