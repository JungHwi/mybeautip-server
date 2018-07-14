package com.jocoos.mybeautip.member.block;

import com.jocoos.mybeautip.restapi.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class BlockController {
  private static BlockService blockService;
  private static BlockRepository blockRepository;
  
  public BlockController(BlockService blockService, BlockRepository blockRepository) {
    this.blockService = blockService;
    this.blockRepository = blockRepository;
  }
  
  /**
   * Block {id}
   * @param principal
   * @param id
   */
  @PostMapping("/api/1/members/me/block/{id}")
  public void blockMember(Principal principal, @PathVariable("id") String id) {
    // TODO: Validation, if i == you, throw exception
    log.debug("Block " + principal.getName() + ": " + id);
    
    Optional<Block> optional
        = blockRepository.findByIAndYou(Long.parseLong(principal.getName()), Long.parseLong(id));
    
    if (optional.isPresent()) {
      log.debug("Already blocked");
    } else {
      Block block = new Block(principal.getName(), id);
      blockRepository.save(block);
    }
  }
  
  /**
   * Unblock {id}
   * @param principal
   * @param id
   */
  @DeleteMapping("/api/1/members/me/block/{id}")
  public void unblockMember(Principal principal, @PathVariable("id") String id) {
    // TODO: Validation, i == you, throw exception
    log.debug("UnBlock " + principal.getName() + ": " + id);
    
    Optional<Block> optional
        = blockRepository.findByIAndYou(Long.parseLong(principal.getName()), Long.parseLong(id));
    if (optional.isPresent()) {
      blockRepository.delete(optional.get());
    } else {
      log.debug("Already unblocked or not exist");
    }
  }
  
  /**
   * Retrieve Member list who I blocked
   * @param principal
   * @return
   */
  @GetMapping("/api/1/members/me/block")
  public ResponseEntity<Response> getBlockMembers(Principal principal,
                                               BlockListRequest request,
                                               HttpServletRequest httpServletRequest) {
    Response response = blockService.getBlockMembers(httpServletRequest.getRequestURI(),
        Long.parseLong(principal.getName()), request.getCursor(), request.getCount());
    return ResponseEntity.ok(response);
  }
}