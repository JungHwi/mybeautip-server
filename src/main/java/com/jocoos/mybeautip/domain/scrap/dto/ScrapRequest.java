package com.jocoos.mybeautip.domain.scrap.dto;

import com.jocoos.mybeautip.domain.scrap.code.ScrapType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScrapRequest {

    private long memberId;

    private ScrapType type;

    private long relationId;

    private boolean isScrap;
}
