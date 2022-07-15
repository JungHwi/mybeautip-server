package com.jocoos.mybeautip.domain.friend.service;

import com.jocoos.mybeautip.domain.friend.converter.FriendInviteInfoConverter;
import com.jocoos.mybeautip.domain.friend.dto.FriendInviteInfoResponse;
import com.jocoos.mybeautip.domain.friend.persistence.domain.FriendInviteInfo;
import com.jocoos.mybeautip.domain.friend.persistence.repository.FriendInviteInfoRepository;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class FriendInviteInfoService {

    private final FriendInviteInfoRepository friendInviteInfoRepository;
    private final FriendInviteInfoConverter friendInviteInfoConverter;

    @Transactional(readOnly = true)
    public FriendInviteInfoResponse getFriendInviteInfo() {
        final List<FriendInviteInfo> infos = friendInviteInfoRepository.findAll();
        return friendInviteInfoConverter.convertToResponse(getFirstWithValid(infos));
    }

    private FriendInviteInfo getFirstWithValid(List<FriendInviteInfo> infos) {
        return infos.stream().findFirst()
                .orElseThrow(() -> new NotFoundException("friend invite info not found"));
    }
}
