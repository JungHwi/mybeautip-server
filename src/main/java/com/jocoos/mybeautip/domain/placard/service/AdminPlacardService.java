package com.jocoos.mybeautip.domain.placard.service;

import com.jocoos.mybeautip.domain.placard.converter.PlacardConverter;
import com.jocoos.mybeautip.domain.placard.dto.PlacardRequest;
import com.jocoos.mybeautip.domain.placard.persistence.domain.Placard;
import com.jocoos.mybeautip.domain.placard.persistence.domain.PlacardDetail;
import com.jocoos.mybeautip.domain.placard.service.dao.PlacardDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminPlacardService {

    private final PlacardDao placardDao;
    private final PlacardConverter converter;


    @Transactional
    public void create(PlacardRequest request) {
        Placard placard = converter.convert(request);
        PlacardDetail placardDetail = converter.convertToDetail(request.getImageUrl());
        placard.addDetail(placardDetail);
        placardDao.save(placard);
    }
}
