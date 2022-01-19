package com.jocoos.mybeautip.support;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import com.jocoos.mybeautip.exception.BadRequestException;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttachmentService {

  private final StorageService storageService;

  public List<String> upload(List<MultipartFile> files, String keyPrefix) throws IOException {
    List<String> attachments = Lists.newArrayList();
    if (files != null && files.size() > 0) {
      int index = 0;

      for (MultipartFile file : files) {
        /**
         * FIXME: How to upload with extension
         */
        String ext = file.getOriginalFilename().split("\\.")[1];
        String key = String.format("%s/%s.%s", keyPrefix, index, ext);
        String path = storageService.upload(file, key);
        log.debug("{}", path);
        attachments.add(path);
        index++;
      }
    }
    return attachments;
  }
}
