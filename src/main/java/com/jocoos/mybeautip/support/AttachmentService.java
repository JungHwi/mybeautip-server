package com.jocoos.mybeautip.support;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import com.google.common.base.Strings;
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
      for (MultipartFile file : files) {
        String path = upload(file, keyPrefix);
        attachments.add(path);
      }
    }
    return attachments;
  }

  public String upload(MultipartFile file, String keyPrefix) throws IOException {
    String filename = DigestUtils.getFilename(file);
    String key = String.format("%s/%s", keyPrefix, filename);
    String path = storageService.upload(file, key);
    log.info("Uploaded {}", path);
    return path;
  }

  public void deleteAttachments(List<String> keys) {
    for (String k : keys) {
      if (!Strings.isNullOrEmpty(k)) {
        String key = k.startsWith("/") ? k.substring(1) : k;
        storageService.delete(key);
      }
    }
  }
}
