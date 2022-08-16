package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.exception.MemberNotFoundException;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import com.jocoos.mybeautip.member.LegacyMemberService;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.block.Block;
import com.jocoos.mybeautip.member.block.BlockRepository;
import com.jocoos.mybeautip.member.block.BlockService;
import com.jocoos.mybeautip.member.block.BlockStatus;
import com.jocoos.mybeautip.notification.MessageService;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.jocoos.mybeautip.member.block.BlockStatus.BLOCK;

@RestController
@RequestMapping(value = "/api/1/members", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class BlockController {
    private static final String MEMBER_NOT_FOUND = "member.not_found";
    private static final String MEMBER_REPORT_BAD_REQUEST = "member.report_bad_request";
    private static final String MEMBER_BLOCK_NOT_FOUND = "member_block.not_found";
    private final BlockService blockService;
    private final LegacyMemberService legacyMemberService;
    private final MessageService messageService;
    private final MemberRepository memberRepository;
    private final BlockRepository blockRepository;

    public BlockController(BlockService blockService,
                           LegacyMemberService legacyMemberService,
                           MessageService messageService,
                           MemberRepository memberRepository,
                           BlockRepository blockRepository) {
        this.blockService = blockService;
        this.legacyMemberService = legacyMemberService;
        this.messageService = messageService;
        this.memberRepository = memberRepository;
        this.blockRepository = blockRepository;
    }

    @PostMapping("/me/blocks")
    public BlockResponse blockMember(@Valid @RequestBody BlockMemberRequest blockMemberRequest,
                                     BindingResult bindingResult,
                                     @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        if (bindingResult.hasErrors()) {
            log.debug("bindingResult: {}", bindingResult);
            throw new BadRequestException("invalid blocks request");
        }

        Long me = legacyMemberService.currentMemberId();
        long you = blockMemberRequest.getMemberId();

        if (me == you) {
            throw new BadRequestException("report_bad_request", messageService.getMessage(MEMBER_REPORT_BAD_REQUEST, lang));
        }
        log.debug("Block " + me + " : " + you);

        Member member = memberRepository.findByIdAndDeletedAtIsNull(you)
                .orElseThrow(() -> new MemberNotFoundException(messageService.getMessage(MEMBER_NOT_FOUND, lang)));

        Block block = blockService.blockMember(me, member);
        return new BlockResponse(block.getId());

    }

    @DeleteMapping("/me/blocks/{id:.+}")
    public void unblockMember(@PathVariable("id") Long id,
                              @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        Long me = legacyMemberService.currentMemberId();
        Block block = blockRepository.findByIdAndMe(id, me)
                .orElseThrow(() -> new NotFoundException("block_not_found", messageService.getMessage(MEMBER_BLOCK_NOT_FOUND, lang)));
        blockService.unblock(block);
    }

    @GetMapping("/me/blocks")
    public CursorResponse getMyBlockMembers(@RequestParam(defaultValue = "50") int count,
                                            @RequestParam(required = false) String cursor,
                                            HttpServletRequest httpServletRequest) {
        return getBlockMembers(httpServletRequest.getRequestURI(), cursor, count);
    }

    @GetMapping("/me/blocked")
    public BlockResponse isBlocked(@RequestParam(name = "member_id") Long memberId) {
        Long me = legacyMemberService.currentMemberId();
        BlockResponse response = new BlockResponse(false);
        blockRepository.findByMeAndMemberYouIdAndStatus(memberId, me, BLOCK)
                .ifPresent(block -> response.setBlocked(true));
        return response;
    }


    private CursorResponse getBlockMembers(String requestUri, String cursor, int count) {
        Date startCursor = (Strings.isBlank(cursor)) ?
                new Date(System.currentTimeMillis()) : new Date(Long.parseLong(cursor));

        PageRequest pageable = PageRequest.of(0, count, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Block> page = blockRepository
                .findByCreatedAtBeforeAndMeAndStatus(startCursor, legacyMemberService.currentMemberId(), pageable, BLOCK);
        List<BlockInfo> result = new ArrayList<>();
        for (Block block : page.getContent()) {
            result.add(new BlockInfo(block, legacyMemberService.getMemberInfo(block.getMemberYou())));
        }

        String nextCursor = null;
        if (result.size() > 0) {
            nextCursor = String.valueOf(result.get(result.size() - 1).getCreatedAt().getTime());
        }
        return new CursorResponse.Builder<>(requestUri, result)
                .withCount(count)
                .withTotalCount(Math.toIntExact(page.getTotalElements()))
                .withCursor(nextCursor).toBuild();
    }

    @Data
    static class BlockMemberRequest {
        private Long memberId;
    }

    @Data
    @NoArgsConstructor
    public class BlockInfo {
        private Long blockId;
        private Date createdAt;
        private MemberInfo member;

        BlockInfo(Block block, MemberInfo member) {
            this.blockId = block.getId();
            this.createdAt = block.getCreatedAt();
            this.member = member;
        }
    }

    @Data
    @NoArgsConstructor
    class BlockResponse {
        Long id;
        Boolean blocked;

        BlockResponse(Long id) {
            this.id = id;
        }

        BlockResponse(Boolean blocked) {
            this.blocked = blocked;
        }

        public BlockResponse(Long id, BlockStatus status) {
            this.id = id;
            this.blocked = BLOCK.equals(status);
        }
    }
}
