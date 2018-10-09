package assignment02;

import java.awt.geom.Point2D;

public class PointSetMath {


  public static double residualError(Point2D[] points1, Point2D[] points2) {
    if (points1.length != points2.length) {
      throw new IllegalArgumentException("Both point sets must have the same length!");
    }
    double error = 0.0;
    for (int i = 0; i < points1.length; i++) {
      error += euclideanDistance(points1[i], points2[i]);
    }
    return error;
  }

  private static double euclideanDistance(Point2D p1, Point2D p2) {
    return Math.sqrt(Math.pow(p1.getX() - p2.getX(), 2.0) + Math.pow(p1.getY() - p2.getY(), 2.0));
  }


}
