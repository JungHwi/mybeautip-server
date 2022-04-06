package com.jocoos.mybeautip.support;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang3.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class DigestUtils {

  public static String getFilename(MultipartFile file) {
    String ext = getExtension(file.getOriginalFilename());
    if (!StringUtils.isBlank(ext)) {
      String hash = getFileHash(file);
      return String.format("%s.%s", hash, ext);
    }
    return getFileHash(file);
  }

  public static String getFilename(File file) {
    String ext = getExtension(file.getName());
    if (!StringUtils.isBlank(ext)) {
      String hash = getFileHash(file);
      return String.format("%s.%s", hash, ext);
    }
    return getFileHash(file);
  }

  private static String getExtension(String filename) {
    if (!StringUtils.isBlank(filename)) {
      String[] split = filename.split("\\.");
      if (split.length > 0) {
        return split[split.length - 1];
      }
    }
    return "";
  }

  public static String getFileHash(InputStream fis) throws IOException {
      MessageDigest digest = null;
    try {
      digest = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      log.warn("{}", e);
      return "";
    }

    //Create byte array to read data in chunks
    byte[] byteArray = new byte[1024];
    int bytesCount = 0;

    //Read file data and update in message digest
    while ((bytesCount = fis.read(byteArray)) != -1) {
      digest.update(byteArray, 0, bytesCount);
    };

    //close the stream; We don't need it now.
    fis.close();

    //Get the hash's bytes
    byte[] bytes = digest.digest();

    //This bytes[] has bytes in decimal format;
    //Convert it to hexadecimal format
    StringBuilder sb = new StringBuilder();
    for(int i=0; i< bytes.length ;i++)
    {
      sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
    }

    //return complete hash
    return sb.toString();
  }

  public static String getFileHash(File file) {
    FileInputStream fis = null;
    try {
      fis = new FileInputStream(file);
      return getFileHash(fis);
    } catch (IOException e) {
      log.error("{}", e);
    }
    return null;
  }

  public static String getFileHash(MultipartFile file) {
    try {
      return getFileHash(file.getInputStream());
    } catch (IOException e) {
      log.error("{}", e);
    }
    return null;
  }
}
