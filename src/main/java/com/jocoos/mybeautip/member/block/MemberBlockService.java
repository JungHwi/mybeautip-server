package com.jocoos.mybeautip.member.block;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberBlockService {

  private final BlockRepository blockRepository;

  public Map<Long, Block> getBlackListByMe(Long me) {
    List<Block> blocks = blockRepository.findByMe(me);
    Map<Long, Block> map = blocks != null ?
        blocks.stream().collect(Collectors.toMap(Block::getYouId, Function.identity())) :
        new HashMap<>();
    new HashMap<>();

    if (map.keySet().size() > 0) {
      log.debug("{} blocked by {}", new ArrayList<>(map.keySet()), me);
    }

    return map;
  }

}
