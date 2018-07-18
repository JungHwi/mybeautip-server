package com.jocoos.mybeautip.member.block;

import com.jocoos.mybeautip.exception.AccessDeniedException;
import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MybeautipRuntimeException;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.restapi.Response;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/1/members/me/blocks", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class BlockController {
  private static MemberService memberService;
  private static BlockService blockService;
  private static BlockRepository blockRepository;
  
  public BlockController(MemberService memberService, BlockService blockService, BlockRepository blockRepository) {
    this.memberService = memberService;
    this.blockService = blockService;
    this.blockRepository = blockRepository;
  }
  
  @PostMapping
  public Response blockMember(@Valid @RequestBody BlockMemberRequest blockMemberRequest,
                              BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      log.debug("bindingResult: {}", bindingResult);
      throw new BadRequestException("invalid blocks request");
    }

    long me = memberService.currentMemberId();
    long you = blockMemberRequest.getMemberId();
    
    if (me == you) {
      throw new BadRequestException("Can't block myself");
    }
    log.debug("Block " + me + " : " + you);
    
    Optional<Block> optional = blockRepository.findByMeAndYou(me, you);
    Response response = new Response();
    
    if (optional.isPresent()) {
      response.setId(optional.get().getId());
    } else {
      Block block = blockRepository.save(new Block(me, you));
      response.setId(block.getId());
    }
    return response;
  }
  
  @DeleteMapping("{id}")
  public void unblockMember(@PathVariable("id") String id) {
    Optional<Block> optional = blockRepository.findById(Long.parseLong(id));
    if (!optional.isPresent()) {
      throw new AccessDeniedException("Can't unblock");
    } else {
      blockRepository.delete(optional.get());
    }
  }
  
  @GetMapping
  public ResponseEntity<Response> getMyBlockMembers(@Valid BlockListRequest request,
                                                    HttpServletRequest httpServletRequest) {
    Response response = blockService.getBlockMembers(httpServletRequest.getRequestURI(),
        memberService.currentMemberId(), request.getCursor(), request.getCount());
    return ResponseEntity.ok(response);
  }
}