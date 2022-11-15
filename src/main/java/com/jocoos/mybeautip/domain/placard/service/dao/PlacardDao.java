package com.jocoos.mybeautip.domain.placard.service.dao;

import com.jocoos.mybeautip.domain.placard.persistence.domain.Placard;
import com.jocoos.mybeautip.domain.placard.persistence.repository.PlacardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PlacardDao {

    private final PlacardRepository repository;

    @Transactional
    public void save(Placard placard) {
        repository.save(placard);
    }
}
