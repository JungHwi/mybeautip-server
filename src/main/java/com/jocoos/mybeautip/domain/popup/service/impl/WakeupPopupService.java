package com.jocoos.mybeautip.domain.popup.service.impl;

import com.jocoos.mybeautip.domain.member.service.dao.DormantMemberDao;
import com.jocoos.mybeautip.domain.popup.service.PopupTypeService;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WakeupPopupService implements PopupTypeService {

    private final DormantMemberDao dao;

    @Override
    public boolean isPopup(Member member) {
        // Client 에서 요청시점에 노출되는 것이 아니라 특정 시점에만 노출되는 것이라서 이 메소드에서는 항상 false 로 리턴.
        return false;
    }
}
