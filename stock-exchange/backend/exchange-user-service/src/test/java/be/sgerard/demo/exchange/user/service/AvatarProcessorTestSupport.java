package be.sgerard.demo.exchange.user.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Base64;
import javax.imageio.ImageIO;

/**
 *
 * Helpers building and inspecting base64 images in tests.
 *
 */
public final class AvatarProcessorTestSupport {

  /**
   *
   * Utility class.
   *
   */
  private AvatarProcessorTestSupport() {
  }

  /**
   *
   * @param width image width
   * @param height image height
   * @return a plain PNG of the given size, encoded in base64
   *
   */
  public static String image(int width, int height) {
    try {
      final ByteArrayOutputStream output = new ByteArrayOutputStream();
      ImageIO.write(new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB), "png", output);
      return Base64.getEncoder().encodeToString(output.toByteArray());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /**
   *
   * @param base64 a base64 encoded image
   * @return its width
   *
   */
  public static int width(String base64) {
    return read(base64).getWidth();
  }

  /**
   *
   * @param base64 a base64 encoded image
   * @return its height
   *
   */
  public static int height(String base64) {
    return read(base64).getHeight();
  }

  /**
   *
   * Decodes a base64 image.
   *
   */
  private static BufferedImage read(String base64) {
    try {
      return ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(base64)));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
