package com.jocoos.mybeautip.domain.friend.api.front;


import com.jocoos.mybeautip.domain.friend.dto.FriendInviteInfoResponse;
import com.jocoos.mybeautip.domain.friend.service.FriendInviteInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/")
@RestController
public class FriendInviteInfoController {

    private final FriendInviteInfoService friendInviteInfoService;

    @GetMapping("/1/friends/invite-info")
    public ResponseEntity<FriendInviteInfoResponse> getFriendInviteInfo() {
        return ResponseEntity.ok(friendInviteInfoService.getFriendInviteInfo());
    }
}
