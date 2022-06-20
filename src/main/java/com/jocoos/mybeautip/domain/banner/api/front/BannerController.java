package com.jocoos.mybeautip.domain.banner.api.front;

import com.jocoos.mybeautip.domain.banner.code.BannerLinkType;
import com.jocoos.mybeautip.domain.banner.code.TabType;
import com.jocoos.mybeautip.domain.banner.dto.BannerResponse;
import com.jocoos.mybeautip.domain.banner.vo.BannerLink;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static com.jocoos.mybeautip.global.constant.MybeautipConstant.DEFAULT_AVATAR_URL;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class BannerController {

    @GetMapping("/1/banner")
    public ResponseEntity<List<BannerResponse>> getBannerList(@RequestParam(defaultValue = "TOTAL") TabType tabType) {
        List<BannerResponse> bannerList = new ArrayList<>();

        BannerLink bannerLink = BannerLink.builder()
                .bannerLinkType(BannerLinkType.EVENT)
                .parameter("10")
                .build();

        BannerResponse bannerResponse = BannerResponse.builder()
                .bannerUrl(DEFAULT_AVATAR_URL)
                .bannerLink(bannerLink)
                .build();

        bannerList.add(bannerResponse);

        return new ResponseEntity<>(bannerList, HttpStatus.OK);
    }
}
