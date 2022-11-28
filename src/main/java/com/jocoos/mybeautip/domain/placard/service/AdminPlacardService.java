package com.jocoos.mybeautip.domain.placard.service;

import com.jocoos.mybeautip.domain.placard.converter.PlacardConverter;
import com.jocoos.mybeautip.domain.placard.dto.AdminPlacardResponse;
import com.jocoos.mybeautip.domain.placard.dto.PatchPlacardRequest;
import com.jocoos.mybeautip.domain.placard.dto.PlacardRequest;
import com.jocoos.mybeautip.domain.placard.persistence.domain.Placard;
import com.jocoos.mybeautip.domain.placard.persistence.domain.PlacardDetail;
import com.jocoos.mybeautip.domain.placard.service.dao.PlacardDao;
import com.jocoos.mybeautip.domain.placard.vo.PlacardSearchCondition;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AdminPlacardService {

    private final PlacardDao placardDao;
    private final PlacardConverter converter;

    @Transactional(readOnly = true)
    public PageResponse<AdminPlacardResponse> getPlacards(PlacardSearchCondition condition) {
        Page<AdminPlacardResponse> page = placardDao.getPlacards(condition);
        return new PageResponse<>(page.getTotalElements(), page.getContent());
    }

    @Transactional
    public AdminPlacardResponse create(PlacardRequest request) {
        Placard placard = converter.convert(request);
        setPlacardDetail(placard, request.getImageUrl());
        placardDao.save(placard);
        return new AdminPlacardResponse(placard);
    }

    @Transactional
    public Long delete(Long placardId) {
        placardDao.delete(placardId);
        return placardId;
    }

    @Transactional
    public Long edit(Long placardId, PatchPlacardRequest request) {
        Placard placard = placardDao.getById(placardId);
        request.edit(placard);
        return placard.getId();
    }

    @Transactional
    public Long topFix(Long placardId, boolean isTopFix) {
        Placard placard = placardDao.getById(placardId);
        placard.topFix(isTopFix);
        changeSorting(placardId, isTopFix);
        return placard.getId();

    }

    @Transactional
    public void arrange(List<Long> ids) {
        placardDao.arrangeByIndex(ids);
    }

    @Transactional
    public Long changeStatus(Long placardId, boolean isActive) {
        Placard placard = placardDao.getById(placardId);
        placard.changeStatus(isActive);
        return placard.getId();
    }

    private void setPlacardDetail(Placard placard, String imageUrl) {
        PlacardDetail placardDetail = converter.convertToDetail(imageUrl);
        placard.initDetail(placardDetail);
    }

    private void changeSorting(Long placardId, boolean isTopFix) {
        if (isTopFix) {
            placardDao.addToFrontOrder(placardId);
        } else {
            placardDao.sortingToNull(placardId);
        }
    }
}
