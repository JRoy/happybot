package io.github.jroy.happybot.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


/**
 * @author JRoy (Joshua Roy)
 */
public class TextGeneration {

  public static BufferedImage card;
  public static BufferedImage background;
  private static Font productSansBold;
  private static BufferedImage progressBar1;
  private static BufferedImage progressBar2;
  private static BufferedImage progressBar3;
  private static BufferedImage progressBar4;
  private static BufferedImage progressBar5;
  private static BufferedImage progressBar6;
  private static BufferedImage progressBar7;
  private static BufferedImage progressBar8;

  static {
    try {
      productSansBold = Font.createFont(Font.TRUETYPE_FONT, new File("res/sans-bold.ttf"));

      card = ImageIO.read(new File("res/card.png"));
      background = ImageIO.read(new File("res/background.png"));
      progressBar1 = ImageIO.read(new File("res/progress-bar1.png"));
      progressBar2 = ImageIO.read(new File("res/progress-bar2.png"));
      progressBar3 = ImageIO.read(new File("res/progress-bar3.png"));
      progressBar4 = ImageIO.read(new File("res/progress-bar4.png"));
      progressBar5 = ImageIO.read(new File("res/progress-bar5.png"));
      progressBar6 = ImageIO.read(new File("res/progress-bar6.png"));
      progressBar7 = ImageIO.read(new File("res/progress-bar7.png"));
      progressBar8 = ImageIO.read(new File("res/progress-bar8.png"));
    } catch (FontFormatException | IOException e) {
      e.printStackTrace();
    }
  }

  public static BufferedImage calculateProgressId(float current, float goal) {
    float diff = current / goal;
    if (diff >= 0.875) {
      return progressBar8;
    } else if (diff >= 0.75) {
      return progressBar7;
    } else if (diff >= 0.625) {
      return progressBar6;
    } else if (diff >= 0.5) {
      return progressBar5;
    } else if (diff >= 0.375) {
      return progressBar4;
    } else if (diff >= 0.25) {
      return progressBar3;
    } else if (diff >= 0.125) {
      return progressBar2;
    } else {
      return progressBar1;
    }
  }

  public static BufferedImage resize(BufferedImage img, int height, int width) {
    Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = resized.createGraphics();
    g2d.drawImage(tmp, 0, 0, null);
    g2d.dispose();
    return resized;
  }

  public static BufferedImage circleize(BufferedImage image) {
    int w = image.getWidth();
    BufferedImage circleBuffer = new BufferedImage(w, w, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = circleBuffer.createGraphics();
    g2.setClip(new Ellipse2D.Float(0, 0, w, w));
    g2.drawImage(image, 0, 0, w, w, null);
    return circleBuffer;
  }

  public static BufferedImage writeImage(BufferedImage image, BufferedImage overlay, int x, int y) {
    BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

    Graphics2D w = (Graphics2D) newImage.getGraphics();
    w.drawImage(image, 0, 0, null);
    w.drawImage(overlay, x, y, null);
    w.dispose();
    return newImage;
  }

  public static BufferedImage writeText(BufferedImage image, String text, Color color, float fontSize, int x, int y) {
    BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

    Graphics2D w = (Graphics2D) newImage.getGraphics();
    w.drawImage(image, 0, 0, null);
    w.setColor(color);
    w.setFont(productSansBold.deriveFont(fontSize));

    w.drawString(text, x, y);
    w.dispose();
    return newImage;
  }

  public static BufferedImage writeTextCenter(BufferedImage image, String text, Color color, float fontSize, int xOffset, int yOffset) {
    BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

    Graphics2D w = (Graphics2D) newImage.getGraphics();
    w.drawImage(image, 0, 0, null);
    w.setColor(color);
    w.setFont(productSansBold.deriveFont(fontSize));
    FontMetrics fontMetrics = w.getFontMetrics();
    Rectangle2D rect = fontMetrics.getStringBounds(text, w);

    int centerX = (image.getWidth() - (int) rect.getWidth()) / 2;
    int centerY = image.getHeight() / 2;

    w.drawString(text, centerX + xOffset, centerY + yOffset);
    w.dispose();
    return newImage;
  }
}
