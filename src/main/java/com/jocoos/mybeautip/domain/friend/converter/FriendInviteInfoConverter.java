package com.jocoos.mybeautip.domain.friend.converter;

import com.jocoos.mybeautip.domain.friend.dto.FriendInviteInfoResponse;
import com.jocoos.mybeautip.domain.friend.persistence.domain.FriendInviteInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FriendInviteInfoConverter {
    FriendInviteInfoResponse convertToResponse(FriendInviteInfo info);
}
