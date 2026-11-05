package be.sgerard.demo.user.service;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * Reduces an uploaded image to the maximum avatar size and returns it as a base64 encoded PNG.
 *
 */
@Component
public class AvatarProcessor {

  /**
   *
   * Maximum width and height, in pixels, of a stored avatar.
   *
   */
  public static final int MAX_SIZE = 128;

  /**
   *
   * Maximum number of pixels accepted in a source image, protecting the server against huge images.
   *
   */
  private static final long MAX_SOURCE_PIXELS = 25_000_000L;

  /**
   *
   * Decodes the given base64 image (optionally a {@code data:} URI), shrinks it so that it fits in
   * {@value #MAX_SIZE}x{@value #MAX_SIZE} pixels while keeping its aspect ratio, and encodes the result
   * as a base64 PNG. Smaller images are never enlarged.
   *
   * @param base64Image the source image encoded in base64
   * @return the reduced image, as a base64 encoded PNG
   * @throws ResponseStatusException with status 400 when the content is not a supported image
   *
   */
  public String process(String base64Image) {
    final byte[] source = decode(base64Image);
    try (ImageInputStream input = ImageIO.createImageInputStream(new ByteArrayInputStream(source))) {
      final Iterator<ImageReader> readers = input == null ? null : ImageIO.getImageReaders(input);
      if (readers == null || !readers.hasNext()) {
        throw invalid("Avatar is not a supported image (PNG, JPEG, GIF or BMP expected)");
      }
      final ImageReader reader = readers.next();
      try {
        reader.setInput(input);
        if ((long) reader.getWidth(0) * reader.getHeight(0) > MAX_SOURCE_PIXELS) {
          throw invalid("Avatar image is too large");
        }
        return encode(shrink(reader.read(0)));
      } finally {
        reader.dispose();
      }
    } catch (IOException | RuntimeException e) {
      if (e instanceof ResponseStatusException status) {
        throw status;
      }
      throw invalid("Avatar image cannot be read");
    }
  }

  /**
   *
   * Decodes the base64 content, ignoring an optional {@code data:} URI prefix.
   *
   */
  private byte[] decode(String base64Image) {
    final String content = base64Image.contains(",") ? base64Image.substring(base64Image.indexOf(',') + 1) : base64Image;
    try {
      return Base64.getMimeDecoder().decode(content.trim());
    } catch (IllegalArgumentException e) {
      throw invalid("Avatar is not valid base64");
    }
  }

  /**
   *
   * Scales the image down to fit in the maximum size; images already small enough are only normalized to ARGB.
   *
   */
  private BufferedImage shrink(BufferedImage image) {
    final double ratio = Math.min(1.0, (double) MAX_SIZE / Math.max(image.getWidth(), image.getHeight()));
    final int width = Math.max(1, (int) Math.round(image.getWidth() * ratio));
    final int height = Math.max(1, (int) Math.round(image.getHeight() * ratio));
    final Image scaled = ratio < 1.0 ? image.getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING) : image;
    final BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    final Graphics2D graphics = result.createGraphics();
    try {
      graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
      graphics.drawImage(scaled, 0, 0, null);
    } finally {
      graphics.dispose();
    }
    return result;
  }

  /**
   *
   * Encodes the image as a base64 PNG.
   *
   */
  private String encode(BufferedImage image) throws IOException {
    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    ImageIO.write(image, "png", output);
    return Base64.getEncoder().encodeToString(output.toByteArray());
  }

  /**
   *
   * Builds the 400 response used for every invalid avatar.
   *
   */
  private ResponseStatusException invalid(String message) {
    return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
  }
}
