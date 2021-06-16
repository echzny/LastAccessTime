package com.echzny.LastAccessTime;

import lombok.extern.slf4j.Slf4j;
import lombok.val;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Objects;

@Slf4j
public class LastAccessTime {
  static String extension = "png";
  static Path path = null;

  public static void main(String[] args) {
    try {
      saveImageToTemporaryDirectory(
          ImageIO.read(LastAccessTime.class.getResourceAsStream("sample.png")));
      log.info("lastAccessTime: " + getLastAccessTime().toString());
      Thread.sleep(500);
      val image = ImageIO.read(path.toFile());
      if (Objects.isNull(image)) {
        log.error("image is null");
      } else {
        log.info("lastAccessTime: " + getLastAccessTime().toString());
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  public static void saveImageToTemporaryDirectory(BufferedImage image)
      throws IOException {
    path = Files.createTempFile(Paths.get(System.getProperty("java.io.tmpdir")),
        "image", extension);
    ImageIO.write(image, extension, path.toFile());
  }

  public static FileTime getLastAccessTime() throws IOException {
    val attrs = Files.readAttributes(path, BasicFileAttributes.class);
    return attrs.lastAccessTime();
  }
}
