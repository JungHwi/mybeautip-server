package com.jocoos.mybeautip.domain.vod.service;

import com.jocoos.mybeautip.domain.broadcast.dto.HeartCountResponse;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastCategory;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastCategoryDao;
import com.jocoos.mybeautip.domain.member.service.dao.MemberDao;
import com.jocoos.mybeautip.domain.scrap.dto.ScrapRequest;
import com.jocoos.mybeautip.domain.scrap.dto.ScrapResponseV2;
import com.jocoos.mybeautip.domain.scrap.persistence.domain.Scrap;
import com.jocoos.mybeautip.domain.scrap.service.dao.ScrapDao;
import com.jocoos.mybeautip.domain.vod.code.VodStatus;
import com.jocoos.mybeautip.domain.vod.dto.VodResponse;
import com.jocoos.mybeautip.domain.vod.persistence.domain.Vod;
import com.jocoos.mybeautip.domain.vod.persistence.domain.VodReport;
import com.jocoos.mybeautip.domain.vod.service.dao.VodDao;
import com.jocoos.mybeautip.domain.vod.service.dao.VodReportDao;
import com.jocoos.mybeautip.domain.vod.vo.VodSearchCondition;
import com.jocoos.mybeautip.global.dto.ReportCountResponse;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.vo.CursorPaging;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jocoos.mybeautip.domain.scrap.code.ScrapType.VOD;
import static com.jocoos.mybeautip.global.exception.ErrorCode.*;

@RequiredArgsConstructor
@Service
public class VodService {

    private final VodDao vodDao;
    private final MemberDao memberDao;
    private final VodReportDao reportDao;
    private final ScrapDao scrapDao;
    private final BroadcastCategoryDao categoryDao;

    @Transactional(readOnly = true)
    public List<VodResponse> getList(long categoryId, CursorPaging<Long> cursorPaging, Pageable pageable) {
        VodSearchCondition condition = VodSearchCondition.builder()
                .categoryIds(getCategories(categoryId))
                .status(VodStatus.AVAILABLE)
                .cursorPaging(cursorPaging)
                .pageable(pageable)
                .isVisible(true)
                .build();
        return vodDao.getListWithMember(condition);
    }

    @Transactional(readOnly = true)
    public VodResponse get(long vodId) {
        Vod vod = vodDao.get(vodId);
        if (!vod.canWatch()) {
            throw new BadRequestException(ACCESS_DENIED, "vod is not watchable");
        }
        Member member = memberDao.getMember(vod.getMemberId());
        return new VodResponse(vod, member);
    }

    @Transactional
    public ReportCountResponse report(long vodId, long reporterId, String description) {
        validIsFirstReport(vodId, reporterId);
        Vod reportedVod = vodDao.get(vodId);
        VodReport vodReport = new VodReport(reportedVod, reporterId, description);
        reportDao.save(vodReport);

        vodDao.addReportCountAndFlush(vodId,1);
        Vod updatedVod = vodDao.get(vodId);
        return new ReportCountResponse(updatedVod.getId(), updatedVod.getReportCount());
    }

    // TODO Need to use Redis or DynamoDb for concurrency control of many requests
    @Transactional
    public HeartCountResponse addHeartCount(long vodId, int addCount) {
        Vod vod = vodDao.getForUpdate(vodId);
        vod.addHeartCount(addCount);
        return new HeartCountResponse(vod.getId(), vod.getTotalHeartCount());
    }

    @Transactional
    public ScrapResponseV2 scrap(Long vodId, Long memberId, boolean isScrap) {
        if (isScrap) {
            validIsFirstScrap(vodId, memberId);
        }
        ScrapRequest request = ScrapRequest.builder()
                .type(VOD)
                .relationId(vodId)
                .memberId(memberId)
                .isScrap(isScrap)
                .build();
        Scrap scrap = scrapDao.scrap(request);
        return ScrapResponseV2.from(scrap);
    }

    private List<Long> getCategories(long categoryId) {
        return categoryDao.getCategories(categoryId)
                .stream()
                .map(BroadcastCategory::getId)
                .toList();
    }

    private void validIsFirstReport(long vodId, long reporterId) {
        if (reportDao.exist(vodId, reporterId)) {
            throw new BadRequestException(ALREADY_REPORT);
        }
    }

    private void validIsFirstScrap(Long vodId, Long memberId) {
        if (scrapDao.isScrap(VOD, memberId, vodId)) {
            throw new BadRequestException(ALREADY_SCRAP);
        }
    }
}
