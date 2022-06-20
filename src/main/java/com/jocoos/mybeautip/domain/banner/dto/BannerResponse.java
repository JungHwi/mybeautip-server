package com.jocoos.mybeautip.domain.banner.dto;

import com.jocoos.mybeautip.domain.banner.vo.BannerLink;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BannerResponse {
    private String bannerUrl;

    private BannerLink bannerLink;
}
