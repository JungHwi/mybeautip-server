package com.jocoos.mybeautip.admin;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.member.LegacyMemberService;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.MemberRoleInfo;
import com.jocoos.mybeautip.member.following.Following;
import com.jocoos.mybeautip.member.following.FollowingRepository;
import com.jocoos.mybeautip.member.point.MemberPointService;
import com.jocoos.mybeautip.security.MyBeautipUserDetails;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/api/admin")
public class AdminMemberController {

    private final LegacyMemberService legacyMemberService;
    private final PasswordEncoder passwordEncoder;
    private final AdminMemberRepository adminMemberRepository;
    private final MemberRepository memberRepository;
    private final FollowingRepository followingRepository;
    private final MemberPointService memberPointService;

    public AdminMemberController(LegacyMemberService legacyMemberService,
                                 PasswordEncoder passwordEncoder,
                                 AdminMemberRepository adminMemberRepository,
                                 MemberRepository memberRepository,
                                 FollowingRepository followingRepository,
                                 MemberPointService memberPointService) {
        this.legacyMemberService = legacyMemberService;
        this.passwordEncoder = passwordEncoder;
        this.adminMemberRepository = adminMemberRepository;
        this.memberRepository = memberRepository;
        this.followingRepository = followingRepository;
        this.memberPointService = memberPointService;
    }

    @GetMapping("/me")
    public ResponseEntity<MemberRoleInfo> getMe() {
        MyBeautipUserDetails myBeautipUserDetails = legacyMemberService.currentUserDetails();

        GrantedAuthority authority = myBeautipUserDetails.getAuthorities().stream().findAny().orElseThrow(() ->
                new MemberNotFoundException("role not found"));

        MemberRoleInfo memberRoleInfo;
        switch (authority.getAuthority()) {
            case "ROLE_ADMIN": {
                memberRoleInfo = new MemberRoleInfo(myBeautipUserDetails.getMember(), 0);
                break;
            }
            case "ROLE_STORE": {
                memberRoleInfo = new MemberRoleInfo(myBeautipUserDetails.getMember(), 1);
                AdminMember adminMember = adminMemberRepository.findByMemberId(memberRoleInfo.getId())
                        .orElseThrow(() -> new MemberNotFoundException("member_not_found", "invalid member id"));

                if (adminMember.getStore() != null) {
                    memberRoleInfo.setStoreId(adminMember.getStore().getId());
                }
                break;
            }
            default:
                throw new MemberNotFoundException("role not found");
        }

        return new ResponseEntity<>(memberRoleInfo, HttpStatus.OK);
    }

    @PostMapping(value = "/admins", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity saveAdmin(@Valid @RequestBody CreateAdminRequest request,
                                    BindingResult bindingResult) {
        log.debug("request: {}", request);

        if (bindingResult.hasErrors()) {
            throw new BadRequestException(bindingResult.getFieldError());
        }

        if (adminMemberRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("email_duplicated", "invalid email");
        }

        AdminMember adminMember = new AdminMember();
        BeanUtils.copyProperties(request, adminMember);

        adminMember.setPassword(passwordEncoder.encode(request.getPassword()));

        Member member = memberRepository.findById(request.getMemberId()).orElseThrow(() -> new MemberNotFoundException("member_not_found", "invalid member_id"));
        adminMember.setMember(member);
        adminMember.setCreatedAt(new Date());

        adminMemberRepository.save(adminMember);

        log.debug("{}", adminMember);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PostMapping("/members/followings")
    public void followMember(@Valid @RequestBody AdminFollowingMemberRequest request) {
        Member me = memberRepository.findByIdAndVisibleIsTrue(request.getMe())
                .orElseThrow(() -> new MemberNotFoundException(request.getMe()));

        Member you = memberRepository.findByIdAndVisibleIsTrue(request.getYou())
                .orElseThrow(() -> new MemberNotFoundException(request.getYou()));

        if (request.getMe().equals(request.getYou())) {
            throw new BadRequestException("bad_request", "Bad request - me and you cannot be the same.");
        }

        Following following = followingRepository.findByMemberMeIdAndMemberYouId(me.getId(), you.getId()).orElse(null);

        if (following == null) {
            legacyMemberService.followMember(me, you);
        } else {  // Already followed
            throw new BadRequestException("bad_request", "Already follow");
        }
    }


    @PostMapping("/members/points")
    public ResponseEntity presentPoint(@Valid @RequestBody CreateMemberPointRequest request,
                                       BindingResult result) {

        if (result.hasErrors()) {
            throw new BadRequestException(result.getFieldError());
        }

        Date expiryAt = null;
        if (StringUtils.isBlank(request.getExpiryAt())) {
            expiryAt = Dates.afterMonthsFromNow(1);
        } else {
            expiryAt = Dates.getRecommendedDate(request.getExpiryAt());
        }

        log.debug("point: {}, expiry date: {}", request.getPoint(), expiryAt);
        memberPointService.presentPoint(request.getMemberId(), request.getPoint(), expiryAt);
        return new ResponseEntity(HttpStatus.CREATED);
    }


    @Data
    public static class AdminFollowingMemberRequest {
        @NotNull
        private Long me;

        @NotNull
        private Long you;
    }

    @Data
    public static class CreateAdminRequest {

        @NotNull
        @Size(max = 50)
        private String email;
        @NotNull
        @Size(max = 60)
        private String password;
        @NotNull
        private Long memberId;
    }

    @Data
    public static class CreateMemberPointRequest {
        @NotNull
        private Long memberId;

        @NotNull
        private int point;

        private String expiryAt;
    }
}
