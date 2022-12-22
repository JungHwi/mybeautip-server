package com.jocoos.mybeautip.domain.placard.service;

import com.jocoos.mybeautip.domain.placard.converter.PlacardConverter;
import com.jocoos.mybeautip.domain.placard.dto.PlacardResponse;
import com.jocoos.mybeautip.domain.placard.persistence.domain.Placard;
import com.jocoos.mybeautip.domain.placard.persistence.repository.PlacardRepository;
import com.jocoos.mybeautip.domain.placard.vo.PlacardSearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

import static com.jocoos.mybeautip.domain.placard.code.PlacardStatus.ACTIVE;
import static com.jocoos.mybeautip.domain.placard.code.PlacardTabType.HOME;

@Service
@RequiredArgsConstructor
public class PlacardService {

    private final PlacardRepository placardRepository;

    private final PlacardConverter placardConverter;

    public List<PlacardResponse> getActivePlacards() {
        PlacardSearchCondition condition = PlacardSearchCondition.builder()
                .status(ACTIVE)
                .type(HOME)
                .between(ZonedDateTime.now())
                .build();
        List<Placard> placards = placardRepository.getPlacards(condition);
        return placardConverter.convertToResponse(placards, HOME);
    }
}
