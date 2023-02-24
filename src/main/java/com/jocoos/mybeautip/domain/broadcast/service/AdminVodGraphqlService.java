package com.jocoos.mybeautip.domain.broadcast.service;

import com.jocoos.mybeautip.domain.broadcast.dto.VodInput;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Vod;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.VodReport;
import com.jocoos.mybeautip.domain.broadcast.service.dao.VodDao;
import com.jocoos.mybeautip.domain.broadcast.service.dao.VodReportDao;
import com.jocoos.mybeautip.domain.broadcast.vo.VodSearchCondition;
import com.jocoos.mybeautip.domain.member.service.dao.MemberDao;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static com.jocoos.mybeautip.global.util.ParentChildMapUtil.parentChildListMapFrom;
import static com.jocoos.mybeautip.global.util.ParentChildMapUtil.parentChildMapFrom;

@RequiredArgsConstructor
@Service
public class AdminVodGraphqlService {

    private final VodDao vodDao;
    private final VodReportDao reportDao;
    private final MemberDao memberDao;

    @Transactional(readOnly = true)
    public List<Vod> getList(VodSearchCondition condition) {
        return vodDao.getList(condition);
    }

    @Transactional(readOnly = true)
    public Vod get(long id) {
        return vodDao.get(id);
    }

    @Transactional
    public Vod edit(VodInput vodInput) {
        Vod vod = vodDao.get(vodInput.id());
        vod.edit(vodInput.title(), vodInput.thumbnail(),  vodInput.isVisible());
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
}
