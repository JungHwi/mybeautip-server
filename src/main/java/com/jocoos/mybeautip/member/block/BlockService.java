package com.jocoos.mybeautip.member.block;

import com.jocoos.mybeautip.member.MemberController;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.restapi.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.domain.PageRequest.of;

@Service
@Slf4j
public class BlockService {
  private static BlockRepository blockRepository;
  private static MemberRepository memberRepository;
  
  public BlockService(BlockRepository blockRepository, MemberRepository memberRepository) {
    this.blockRepository = blockRepository;
    this.memberRepository = memberRepository;
  }
  
  public Response getBlockMembers(String requestUri, long i, String cursor, int count) {
    long startCursor;
    
    if (Strings.isBlank(cursor)) {
      startCursor = System.currentTimeMillis();
    } else {
      startCursor = Long.parseLong(cursor);
    }
    
    List<MemberController.MemberInfo> list = new ArrayList<>();
    Slice<Block> slice = blockRepository.findAllByI(i, startCursor, of(0, count));
    for (Block block : slice.getContent()) {
      list.add(new MemberController.MemberInfo(memberRepository.getOne(block.getYou())));
    }
    
    Response<MemberController.MemberInfo> response = new Response();
    if (slice.getContent().size() >= count) {
      Block block = slice.getContent().get(slice.getSize() - 1);
      String nextCursor = String.valueOf(block.getCreatedAt());
      String nextRef = response.generateNextRef(requestUri, nextCursor, count);
      response.setNextCursor(nextCursor);
      response.setNextRef(nextRef);
    } else {
      response.setNextCursor("");
      response.setNextRef("");
    }
    
    response.setContent(list);
    return response;
  }
}