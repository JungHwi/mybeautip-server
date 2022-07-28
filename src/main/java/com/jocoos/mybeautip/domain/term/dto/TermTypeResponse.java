package com.jocoos.mybeautip.domain.term.dto;


import com.jocoos.mybeautip.domain.term.code.TermType;
import lombok.AllArgsConstructor;
import lombok.Getter;

// 2021-07-28 현재 서버에서 약관 관리하지 않기 때문에 임시로 만듦, 추후 서버에서 약관 관리한다면 삭제
@Getter
@AllArgsConstructor
public class TermTypeResponse {
    private TermType termType;
    private Boolean isAccept;
}
