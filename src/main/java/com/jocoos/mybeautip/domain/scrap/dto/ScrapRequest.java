package com.jocoos.mybeautip.domain.scrap.dto;

import com.jocoos.mybeautip.domain.scrap.code.ScrapType;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScrapRequest {

    private long memberId;

    private ScrapType type;

    private long relationId;

    private Boolean isScrap;
}
