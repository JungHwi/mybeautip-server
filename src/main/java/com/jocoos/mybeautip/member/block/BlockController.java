package com.jocoos.mybeautip.member.block;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MybeautipRuntimeException;
import com.jocoos.mybeautip.restapi.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/1/members/me/blocks", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class BlockController {
  private static BlockService blockService;
  private static BlockRepository blockRepository;
  
  public BlockController(BlockService blockService, BlockRepository blockRepository) {
    this.blockService = blockService;
    this.blockRepository = blockRepository;
  }
  
  @PostMapping
  public Response blockMember(Principal principal,
                              @Valid @RequestBody BlockMemberRequest blockMemberRequest) {
    // TODO: will be replaced common method
    long me = Long.valueOf(principal.getName());
    long you = blockMemberRequest.getMemberId();
    
    if (me == you) {
      throw new BadRequestException("Can't block myself");
    }
    log.debug("Block " + principal.getName() + ": " + you);
    
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
      throw new MybeautipRuntimeException("Access denied", "Can't unfollow");
    } else {
      blockRepository.delete(optional.get());
    }
  }
  
  @GetMapping
  public ResponseEntity<Response> getMyBlockMembers(Principal principal,
                                               BlockListRequest request,
                                               HttpServletRequest httpServletRequest) {
    Response response = blockService.getBlockMembers(httpServletRequest.getRequestURI(),
        Long.parseLong(principal.getName()), request.getCursor(), request.getCount());
    return ResponseEntity.ok(response);
  }
}