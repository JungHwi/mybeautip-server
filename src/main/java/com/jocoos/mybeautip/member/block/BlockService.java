package com.jocoos.mybeautip.member.block;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import static org.springframework.data.domain.PageRequest.of;

import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.restapi.Response;

@Service
@Slf4j
public class BlockService {
  private static BlockRepository blockRepository;
  private static MemberRepository memberRepository;
  
  public BlockService(BlockRepository blockRepository, MemberRepository memberRepository) {
    this.blockRepository = blockRepository;
    this.memberRepository = memberRepository;
  }
  
  public Response getBlockMembers(String requestUri, long me, String cursor, int count) {
    Date startCursor = (Strings.isBlank(cursor)) ?
        new Date(System.currentTimeMillis()) : new Date(Long.parseLong(cursor));
    
    Slice<Block> slice = blockRepository.findAllByMe(me, startCursor, of(0, count));
    List<BlockInfo> list = new ArrayList<>();
    BlockInfo blockInfo;
    for (Block block : slice.getContent()) {
      blockInfo = new BlockInfo(block);
      blockInfo.setMember(new MemberInfo(memberRepository.getOne(block.getYou())));
      list.add(blockInfo);
    }
    
    Response<BlockInfo> response = new Response<>();
    if (slice.getContent().size() >= count) {
      Block block = slice.getContent().get(slice.getSize() - 1);
      String nextCursor = String.valueOf(block.getCreatedAt().getTime());
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