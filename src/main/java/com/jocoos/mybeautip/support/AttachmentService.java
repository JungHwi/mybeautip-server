package com.jocoos.mybeautip.support;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttachmentService {

  private final StorageService storageService;

  public List<String> upload(List<MultipartFile> files, String keyPrefix) throws IOException {
    List<String> attachments = new ArrayList<>();
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

    public void deleteAttachments(String k) {
        if (!StringUtils.isBlank(k)) {
            String key = k.startsWith("/") ? k.substring(1) : k;
            storageService.delete(key);
        }
    }

    public void deleteAttachments(List<String> keys) {
        for (String k : keys) {
            deleteAttachments(k);
        }
    }
}
