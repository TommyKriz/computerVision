package assignment03;

import ij.process.ImageProcessor;
import imagingbook.pub.corners.Corner;

import java.awt.Color;
import java.util.List;

public class CornerVisualization {

  private final static int CORNER_SIZE = 2;

  private final static int CORNER_OVAL_R = 10;

  public static void drawCorners(ImageProcessor ip, List<Corner> corners, Color color) {
    ip.setColor(color);
    int n = 0;
    for (Corner c : corners) {
      drawCorner(ip, c);
      n = n + 1;
    }
  }

  private static void drawCorner(ImageProcessor ip, Corner c) {
    int x = Math.round(c.getX());
    int y = Math.round(c.getY());
    ip.drawLine(x - CORNER_SIZE, y, x + CORNER_SIZE, y);
    ip.drawLine(x, y - CORNER_SIZE, x, y + CORNER_SIZE);
    ip.drawOval(x - CORNER_OVAL_R, y - CORNER_OVAL_R, CORNER_OVAL_R * 2, CORNER_OVAL_R * 2);
  }

}
