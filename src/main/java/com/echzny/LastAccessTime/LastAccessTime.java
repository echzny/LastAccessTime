package com.echzny.LastAccessTime;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.LogManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
public class LastAccessTime extends Application {
  static String extension = "png";
  static Path path = null;

  @Override
  public void start(Stage stage) {
    try {
      log.info("start time: " + LocalDateTime.now());

      saveImageToTemporaryDirectory(ImageIO.read(LastAccessTime.class.getResourceAsStream("sample.png")));
      log.info("default lastAccessTime: " + getLastAccessTime().toString());
      Thread.sleep(1000);

      val image = ImageIO.read(path.toFile());
      Files.setAttribute(path, "lastAccessTime", FileTime.from(Instant.now()));
      // キャッシュが読み込まれるらしく、lastAccessTime は明示的に更新しないと値が変化しない
      if (Objects.isNull(image)) {
        log.error("image is null");
      } else {
        log.info("lastAccessTime when ImageIO.read(): " + getLastAccessTime().toString());
      }
      Thread.sleep(1000);

      val image2 = ImageIO.read(path.toFile());
      Files.setAttribute(path, "lastAccessTime", FileTime.from(Instant.now()));
      if (Objects.isNull(image2)) {
        log.error("image2 is null");
      } else {
        log.info("lastAccessTime when ImageIO.read() again: " + getLastAccessTime().toString());
      }
      Thread.sleep(1000);

      val fxImage = new Image(path.toUri().toString());
      Files.setAttribute(path, "lastAccessTime", FileTime.from(Instant.now()));
      if (Objects.isNull(fxImage)) {
        log.error("fxImage is null");
      } else {
        log.info("lastAccessTime when new Image(): " + getLastAccessTime().toString());
      }
      Thread.sleep(1000);

      Platform.exit();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  public static void main(String[] args) {
    try {
      launch(args);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  public void saveImageToTemporaryDirectory(BufferedImage image)
      throws IOException {
    path = Files.createTempFile(Paths.get(System.getProperty("java.io.tmpdir")),
        "image", extension);
    ImageIO.write(image, extension, path.toFile());
  }

  public FileTime getLastAccessTime() throws IOException {
    val attrs = Files.readAttributes(path, BasicFileAttributes.class);
    return attrs.lastAccessTime();
  }

  public byte[] bufferedImageToByteArray(@NonNull BufferedImage image) {
    val output = new ByteArrayOutputStream();

    try {
      ImageIO.write(image, extension, output);

      return output.toByteArray();
    } catch (IOException e) {
      LogManager.getLogger().error(e.getMessage(), e);

      return null;
    }
  }

  public InputStream bufferedImageToInputStream(@NonNull BufferedImage image) {
    return new ByteArrayInputStream(bufferedImageToByteArray(image));
  }
}
