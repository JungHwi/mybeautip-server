package com.jocoos.mybeautip.domain.member.dto;

import com.jocoos.mybeautip.member.Member;
import lombok.Builder;

// 2023-03-29
// Currently All Activities Cursor Are Id DESC But If Changed Use Other Options
@Builder
public record MemberActivityRequest(Member member,
                                    Long idCursor,
                                    int size) {
}
