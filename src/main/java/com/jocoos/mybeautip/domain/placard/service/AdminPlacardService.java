package com.jocoos.mybeautip.domain.placard.service;

import com.jocoos.mybeautip.client.aws.s3.AwsS3Handler;
import com.jocoos.mybeautip.domain.placard.converter.PlacardConverter;
import com.jocoos.mybeautip.domain.placard.dto.AdminPlacardDetailResponse;
import com.jocoos.mybeautip.domain.placard.dto.AdminPlacardResponse;
import com.jocoos.mybeautip.domain.placard.dto.PatchPlacardRequest;
import com.jocoos.mybeautip.domain.placard.dto.PlacardRequest;
import com.jocoos.mybeautip.domain.placard.persistence.domain.Placard;
import com.jocoos.mybeautip.domain.placard.persistence.domain.PlacardDetail;
import com.jocoos.mybeautip.domain.placard.service.dao.PlacardDao;
import com.jocoos.mybeautip.domain.placard.vo.PlacardSearchCondition;
import com.jocoos.mybeautip.global.dto.FileDto;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jocoos.mybeautip.global.code.UrlDirectory.PLACARD;

@RequiredArgsConstructor
@Service
public class AdminPlacardService {

    private final PlacardDao placardDao;
    private final AwsS3Handler awsS3Handler;
    private final PlacardConverter converter;

    @Transactional(readOnly = true)
    public AdminPlacardDetailResponse getPlacard(Long placardId) {
        Placard placard = placardDao.getById(placardId);
        return new AdminPlacardDetailResponse(placard);
    }

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
        awsS3Handler.copy(request.getImageUrl(), PLACARD.getDirectory());
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
        editFile(request, placard.getImageUrl());
        Placard editedPlacard = request.edit(placard);
        Placard savedPlacard = placardDao.save(editedPlacard);
        return savedPlacard.getId();
    }

    @Transactional
    public Long topFix(Long placardId, boolean isTopFix) {
        Placard placard = placardDao.getById(placardId);
        changeIsTopFixAndSorting(placard, isTopFix);
        return placard.getId();

    }

    @Transactional
    public List<Long> arrange(List<Long> ids) {
        return placardDao.arrangeByIndex(ids);
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

    private void editFile(PatchPlacardRequest request, String originalImageUrl) {
        List<FileDto> fileDto = request.getFileDto(originalImageUrl);
        awsS3Handler.copy(fileDto, PLACARD.getDirectory());
    }

    private void changeIsTopFixAndSorting(Placard placard, boolean isTopFix) {
        if (isTopFix) {
            placard.validActive();
            placardDao.fixAndAddToLastOrder(placard.getId());
        } else {
            placardDao.unFixAndSortingToNull(placard.getId());
        }
    }
}
