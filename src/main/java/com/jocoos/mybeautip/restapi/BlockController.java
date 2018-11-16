package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.exception.AccessDeniedException;
import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.member.block.Block;
import com.jocoos.mybeautip.member.block.BlockRepository;
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
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/1/members", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class BlockController {
  private final MemberService memberService;
  private final MemberRepository memberRepository;
  private final BlockRepository blockRepository;
  
  public BlockController(MemberService memberService,
                         MemberRepository memberRepository,
                         BlockRepository blockRepository) {
    this.memberService = memberService;
    this.memberRepository = memberRepository;
    this.blockRepository = blockRepository;
  }
  
  @PostMapping("/me/blocks")
  public BlockResponse blockMember(@Valid @RequestBody BlockMemberRequest blockMemberRequest,
                              BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      log.debug("bindingResult: {}", bindingResult);
      throw new BadRequestException("invalid blocks request");
    }

    Long me = memberService.currentMemberId();
    if (me == null) {
      throw new MemberNotFoundException("Login required");
    }
    long you = blockMemberRequest.getMemberId();
    
    if (me == you) {
      throw new BadRequestException("Can't block myself");
    }
    log.debug("Block " + me + " : " + you);

    Member member = memberRepository.findByIdAndDeletedAtIsNull(you)
      .orElseThrow(() -> new MemberNotFoundException(you));

    Optional<Block> optional = blockRepository.findByMeAndMemberYouId(me, you);

    if (optional.isPresent()) {
      return new BlockResponse(optional.get().getId());
    } else {
      Block block = blockRepository.save(new Block(me, member));
      return new BlockResponse(block.getId());
    }
  }
  
  @DeleteMapping("/me/blocks/{id:.+}")
  public void unblockMember(@PathVariable("id") String id) {
    Optional<Block> optional = blockRepository.findById(Long.parseLong(id));
    if (!optional.isPresent()) {
      throw new AccessDeniedException("Can't unblock");
    } else {
      blockRepository.delete(optional.get());
    }
  }
  
  @GetMapping("/me/blocks")
  public CursorResponse getMyBlockMembers(@RequestParam(defaultValue = "50") int count,
                                          @RequestParam(required = false) String cursor,
                                          HttpServletRequest httpServletRequest) {
    Long memberId = memberService.currentMemberId();
    if (memberId == null) {
      throw new MemberNotFoundException("Login required");
    }
    return getBlockMembers(httpServletRequest.getRequestURI(), cursor, count);
  }

  @GetMapping("/me/blocked")
  public BlockResponse isBlocked(@RequestParam(name="member_id") Long memberId) {
    Long me = memberService.currentMemberId();
    if (me == null) {
      throw new MemberNotFoundException("Login required");
    }

    BlockResponse response = new BlockResponse(false);
    blockRepository.findByMeAndMemberYouId(memberId, me)
      .ifPresent(block -> response.setBlocked(true));
    return response;
  }


  private CursorResponse getBlockMembers(String requestUri, String cursor, int count) {
    Date startCursor = (Strings.isBlank(cursor)) ?
      new Date(System.currentTimeMillis()) : new Date(Long.parseLong(cursor));

    PageRequest pageable = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "id"));
    Page<Block> page = blockRepository.findByCreatedAtBeforeAndMe(startCursor, memberService.currentMemberId(), pageable);
    List<BlockInfo> result = new ArrayList<>();
    BlockInfo blockInfo;
    for (Block block : page.getContent()) {
      blockInfo = new BlockInfo(block, memberService.getMemberInfo(block.getMemberYou()));
      result.add(blockInfo);
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
  static class BlockMemberRequest {
    private Long memberId;
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
  }
}