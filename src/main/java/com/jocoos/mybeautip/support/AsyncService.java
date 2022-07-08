package com.jocoos.mybeautip.support;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AsyncService {

  @Async
  public void run(Runnable runnable) {
    runnable.run();
  }
}
