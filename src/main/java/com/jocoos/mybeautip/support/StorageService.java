package com.jocoos.mybeautip.support;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {

  String upload(MultipartFile file, String key) throws IOException;
  Resource getResource(String key);
  void delete(String key);
}
