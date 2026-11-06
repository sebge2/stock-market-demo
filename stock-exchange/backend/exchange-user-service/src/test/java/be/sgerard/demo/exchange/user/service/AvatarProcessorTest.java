package be.sgerard.demo.exchange.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * Verifies the reduction of avatars to 128x128 pixels.
 *
 */
class AvatarProcessorTest {

  /**
   *
   * Processor under test.
   *
   */
  private final AvatarProcessor processor = new AvatarProcessor();

  /**
   *
   * A large landscape image is reduced to 128 pixels wide, keeping its aspect ratio, and returned as PNG.
   *
   * @throws IOException on failure
   */
  @Test
  void shrinksLargeImageKeepingAspectRatio() throws IOException {
    final BufferedImage result = decode(processor.process(encode(1000, 500, "png")));

    assertThat(result.getWidth()).isEqualTo(128);
    assertThat(result.getHeight()).isEqualTo(64);
  }

  /**
   *
   * A large portrait JPEG is reduced to 128 pixels high.
   *
   * @throws IOException on failure
   */
  @Test
  void shrinksPortraitJpeg() throws IOException {
    final BufferedImage result = decode(processor.process(encode(300, 600, "jpg")));

    assertThat(result.getWidth()).isEqualTo(64);
    assertThat(result.getHeight()).isEqualTo(128);
  }

  /**
   *
   * A small image is never enlarged and a {@code data:} URI prefix is accepted.
   *
   * @throws IOException on failure
   */
  @Test
  void keepsSmallImageAndAcceptsDataUri() throws IOException {
    final BufferedImage result = decode(processor.process("data:image/png;base64," + encode(40, 20, "png")));

    assertThat(result.getWidth()).isEqualTo(40);
    assertThat(result.getHeight()).isEqualTo(20);
  }

  /**
   *
   * Invalid base64 and non-image content are rejected with a 400.
   *
   */
  @Test
  void rejectsInvalidContent() {
    assertThatThrownBy(() -> processor.process("%%% not base64 %%%")).isInstanceOfSatisfying(
        ResponseStatusException.class, e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
    final String text = Base64.getEncoder().encodeToString("hello".getBytes());
    assertThatThrownBy(() -> processor.process(text)).isInstanceOfSatisfying(
        ResponseStatusException.class, e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
  }

  /**
   *
   * Builds a plain image encoded in base64.
   *
   */
  private static String encode(int width, int height, String format) throws IOException {
    final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    final Graphics2D graphics = image.createGraphics();
    graphics.setColor(Color.BLUE);
    graphics.fillRect(0, 0, width, height);
    graphics.dispose();
    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    ImageIO.write(image, format, output);
    return Base64.getEncoder().encodeToString(output.toByteArray());
  }

  /**
   *
   * Decodes a base64 PNG.
   *
   */
  private static BufferedImage decode(String base64) throws IOException {
    return ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(base64)));
  }
}
