package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.member.following.Following;
import com.jocoos.mybeautip.member.following.FollowingMemberRequest;
import com.jocoos.mybeautip.member.following.FollowingRepository;
import com.jocoos.mybeautip.notification.MessageService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/api/1/members", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class FollowingController {
    private static final String MEMBER_NOT_FOUND = "member.not_found";
    private static final String MEMBER_FOLLOWING_BAD_REQUEST = "member.following_bad_request";
    private static final String FOLLOWING_NOT_FOUND = "following.not_found";
    private final MemberService memberService;
    private final MessageService messageService;
    private final MemberRepository memberRepository;
    private final FollowingRepository followingRepository;

    public FollowingController(MemberService memberService,
                               MessageService messageService,
                               MemberRepository memberRepository,
                               FollowingRepository followingRepository) {
        this.memberService = memberService;
        this.messageService = messageService;
        this.memberRepository = memberRepository;
        this.followingRepository = followingRepository;
    }

    @PostMapping("/me/followings")
    public FollowingResponse followMember(@Valid @RequestBody FollowingMemberRequest followingMemberRequest,
                                          @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        Member me = memberService.currentMember();
        if (me.getId().equals(followingMemberRequest.getMemberId())) {
            throw new BadRequestException("following_bad_request", messageService.getMessage(MEMBER_FOLLOWING_BAD_REQUEST, lang));
        }

        Member you = memberRepository.findByIdAndDeletedAtIsNull(followingMemberRequest.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException(messageService.getMessage(MEMBER_NOT_FOUND, lang)));
        Following following = followingRepository.findByMemberMeIdAndMemberYouId(me.getId(), you.getId()).orElse(null);

        if (following == null) {
            return new FollowingResponse(memberService.followMember(me, you).getId());
        } else {  // Already followed
            return new FollowingResponse(following.getId());
        }
    }

    @DeleteMapping("/me/followings/{id}")
    public void unFollowMember(@PathVariable("id") Long id,
                               @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        Member me = memberService.currentMember();
        Following following = followingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("following_not_found", messageService.getMessage(FOLLOWING_NOT_FOUND, lang)));

        if (!me.equals(following.getMemberMe())) {
            throw new BadRequestException("following_not_found", messageService.getMessage(FOLLOWING_NOT_FOUND, lang));
        }

        memberService.unFollowMember(following);
    }

    @GetMapping("/me/followings")
    public CursorResponse getFollowing(@RequestParam(defaultValue = "50") int count,
                                       @RequestParam(required = false) String cursor,
                                       HttpServletRequest httpServletRequest) {
        Long memberId = memberService.currentMemberId();
        return getFollowings(httpServletRequest.getRequestURI(), memberId, cursor, count);
    }

    @GetMapping("/me/followers")
    public CursorResponse getFollowers(@RequestParam(defaultValue = "50") int count,
                                       @RequestParam(required = false) String cursor,
                                       HttpServletRequest httpServletRequest) {
        Long memberId = memberService.currentMemberId();
        return getFollowers(httpServletRequest.getRequestURI(), memberId, cursor, count);
    }

    @GetMapping("/{id}/followings")
    public CursorResponse getFollowing(@PathVariable("id") Long id,
                                       @RequestParam(defaultValue = "50") int count,
                                       @RequestParam(required = false) String cursor,
                                       HttpServletRequest httpServletRequest) {
        return getFollowings(httpServletRequest.getRequestURI(), id, cursor, count);
    }

    @GetMapping("/{id}/followers")
    public CursorResponse getFollowers(@PathVariable("id") Long id,
                                       @RequestParam(defaultValue = "50") int count,
                                       @RequestParam(required = false) String cursor,
                                       HttpServletRequest httpServletRequest) {
        return getFollowers(httpServletRequest.getRequestURI(), id, cursor, count);
    }


    private CursorResponse getFollowings(String requestUri, long me, String cursor, int count) {
        Date startCursor = (Strings.isBlank(cursor)) ?
                new Date(System.currentTimeMillis()) : new Date(Long.parseLong(cursor));

        PageRequest pageable = PageRequest.of(0, count, Sort.by(Sort.Direction.DESC, "createdAt"));
        Slice<Following> slice = followingRepository.findByCreatedAtBeforeAndMemberMeId(startCursor, me, pageable);

        List<MemberInfo> result = new ArrayList<>();

        for (Following following : slice.getContent()) {
            // Add following id when I follow you
            result.add(memberService.getMemberInfo(following.getMemberYou()));
        }

        String nextCursor = null;
        if (result.size() > 0) {
            nextCursor = String.valueOf(slice.getContent().get(slice.getContent().size() - 1).getCreatedAt().getTime());
        }
        return new CursorResponse.Builder<>(requestUri, result)
                .withCount(count)
                .withCursor(nextCursor).toBuild();
    }

    private CursorResponse getFollowers(String requestUri, long you, String cursor, int count) {
        Date startCursor = (Strings.isBlank(cursor)) ?
                new Date(System.currentTimeMillis()) : new Date(Long.parseLong(cursor));

        PageRequest pageable = PageRequest.of(0, count, Sort.by(Sort.Direction.DESC, "createdAt"));
        Slice<Following> slice = followingRepository.findByCreatedAtBeforeAndMemberYouId(startCursor, you, pageable);

        List<MemberInfo> result = new ArrayList<>();

        for (Following follower : slice.getContent()) {
            // Add following id when I follow
            result.add(memberService.getMemberInfo(follower.getMemberMe()));
        }

        String nextCursor = null;
        if (result.size() > 0) {
            nextCursor = String.valueOf(slice.getContent().get(slice.getContent().size() - 1).getCreatedAt().getTime());
        }
        return new CursorResponse.Builder<>(requestUri, result)
                .withCount(count)
                .withCursor(nextCursor).toBuild();
    }

    @Data
    @AllArgsConstructor
    class FollowingResponse {
        Long id;
    }
}