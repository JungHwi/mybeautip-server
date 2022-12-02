package com.jocoos.mybeautip.domain.placard.service;

import com.jocoos.mybeautip.domain.placard.converter.PlacardConverter;
import com.jocoos.mybeautip.domain.placard.dto.PlacardResponse;
import com.jocoos.mybeautip.domain.placard.persistence.domain.Placard;
import com.jocoos.mybeautip.domain.placard.persistence.repository.PlacardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.jocoos.mybeautip.domain.placard.code.PlacardStatus.ACTIVE;
import static com.jocoos.mybeautip.domain.placard.code.PlacardTabType.HOME;

@Service
@RequiredArgsConstructor
public class PlacardService {

    private final PlacardRepository placardRepository;

    private final PlacardConverter placardConverter;

    public List<PlacardResponse> getPlacardList() {
        List<Placard> placardList = placardRepository.findByActivePlacard(ACTIVE, HOME);
        return placardConverter.convertToResponse(placardList, HOME);
    }
}
