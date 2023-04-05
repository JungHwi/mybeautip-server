package com.jocoos.mybeautip.domain.scrap.vo;

import com.jocoos.mybeautip.domain.scrap.persistence.domain.Scrap;

import java.util.List;

public record ScrapTypeCondition(List<Scrap> scraps,
                                 Long memberId) {
}
