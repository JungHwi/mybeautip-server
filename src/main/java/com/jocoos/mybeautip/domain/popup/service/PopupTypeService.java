package com.jocoos.mybeautip.domain.popup.service;

import com.jocoos.mybeautip.member.Member;
import org.springframework.stereotype.Service;

@Service
public interface PopupTypeService {

    boolean isPopup(Member member);
}
