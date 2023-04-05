package com.jocoos.mybeautip.domain.vod.service;

import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastKey;
import com.jocoos.mybeautip.domain.member.service.dao.MemberDao;
import com.jocoos.mybeautip.domain.vod.dto.VodInput;
import com.jocoos.mybeautip.domain.vod.persistence.domain.Vod;
import com.jocoos.mybeautip.domain.vod.persistence.domain.VodReport;
import com.jocoos.mybeautip.domain.vod.service.dao.VodDao;
import com.jocoos.mybeautip.domain.vod.service.dao.VodReportDao;
import com.jocoos.mybeautip.domain.vod.vo.VodSearchCondition;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.security.MyBeautipUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.jocoos.mybeautip.global.util.ParentChildMapUtil.parentChildListMapFrom;
import static com.jocoos.mybeautip.global.util.ParentChildMapUtil.parentChildMapFrom;

@RequiredArgsConstructor
@Service
public class AdminVodGraphqlService {

    private final VodDao vodDao;
    private final VodFFLService fflService;
    private final VodReportDao reportDao;
    private final MemberDao memberDao;

    @Transactional(readOnly = true)
    public PageResponse<Vod> getList(VodSearchCondition condition) {
        Page<Vod> page = vodDao.getList(condition);
        return new PageResponse<>(page);
    }

    @Transactional(readOnly = true)
    public Vod get(long id) {
        return vodDao.get(id);
    }

    @Transactional
    public Vod edit(VodInput vodInput) {
        Vod vod = vodDao.get(vodInput.id());
        vod.edit(vodInput.title(), vodInput.thumbnailUrl(),  vodInput.isVisible());
        return vod;
    }

    @Transactional(readOnly = true)
    public Map<Vod, List<VodReport>> getVodReportsMap(List<Vod> vodList) {
        return parentChildListMapFrom(
                vodList,
                Vod::getId,
                reportDao::getByVodIdIn,
                VodReport::getVodId
        );
    }

    @Transactional(readOnly = true)
    public Map<VodReport, Member> getVodReportReporterMap(List<VodReport> reports) {
        return parentChildMapFrom(
                reports,
                VodReport::getReporterId,
                memberDao::getMembers,
                Member::getId
        );
    }

    @Transactional(readOnly = true)
    public Map<Vod, Member> getVodMemberMap(List<Vod> vodList) {
        return parentChildMapFrom(
                vodList,
                Vod::getMemberId,
                memberDao::getMembers,
                Member::getId
        );
    }

    public Map<Vod, BroadcastKey> getVodVodKeyMap(List<Vod> vodList) {
        MyBeautipUserDetails userDetails = (MyBeautipUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return vodList.stream()
                .collect(Collectors.toMap(vod -> vod, vod -> fflService.getVodKey(userDetails.getUsername(), vod)));
    }
}
