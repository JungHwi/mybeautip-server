package com.jocoos.mybeautip.testutil.fixture;

import com.jocoos.mybeautip.domain.placard.persistence.domain.Placard;
import com.jocoos.mybeautip.domain.placard.persistence.domain.PlacardDetail;

import java.util.List;
import java.util.stream.IntStream;

import static com.jocoos.mybeautip.domain.placard.code.PlacardLinkType.EVENT;
import static com.jocoos.mybeautip.domain.placard.code.PlacardStatus.ACTIVE;
import static com.jocoos.mybeautip.domain.placard.code.PlacardTabType.HOME;
import static java.time.ZonedDateTime.now;

public class PlacardFixture {

    public static Placard makePlacard() {
        Placard placard = Placard.builder()
                .status(ACTIVE)
                .title("타이틀")
                .description("설명")
                .isTopFix(false)
                .startedAt(now().minusDays(1))
                .endedAt(now().plusDays(1))
                .color("색깔")
                .linkType(EVENT)
                .linkArgument("1")
                .build();

        PlacardDetail placardDetail = createPlacardDetail();
        placardDetail.setPlacard(placard);
        placard.initDetail(placardDetail);

        return placard;
    }

    private static PlacardDetail createPlacardDetail() {
        return PlacardDetail.builder()
                .tabType(HOME)
                .imageFile("imageFile")
                .build();
    }

    public static List<Placard> makePlacards(int placardNum) {
        return IntStream.range(0, placardNum)
                .mapToObj(i -> makePlacard())
                .toList();
    }
}
