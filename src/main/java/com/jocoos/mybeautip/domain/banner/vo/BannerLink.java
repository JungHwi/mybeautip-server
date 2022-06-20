package com.jocoos.mybeautip.domain.banner.vo;

import com.jocoos.mybeautip.domain.banner.code.BannerLinkType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BannerLink {

    private BannerLinkType bannerLinkType;

    private String parameter;
}
