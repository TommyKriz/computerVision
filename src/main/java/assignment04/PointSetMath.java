package assignment04;

import java.awt.Point;
import java.awt.geom.Point2D;

public class PointSetMath {

	public static double getMajorAxis(Point[] pointSet, Point2D centroid) {
		return 0.5 * Math.atan2(
				2 * moment11(pointSet, centroid.getX(), centroid.getY()),
				moment20(pointSet, centroid.getX())
						- moment02(pointSet, centroid.getY()));
	}

	private static double moment11(Point[] pointSet, double centroidX,
			double centroidY) {
		double sum = 0.0;
		for (Point2D p : pointSet) {
			sum += (p.getX() - centroidX) * (p.getY() - centroidY);
		}
		return sum;
	}

	private static double moment20(Point[] pointSet, double centroidX) {
		double sum = 0.0;
		for (Point2D p : pointSet) {
			sum += Math.pow(p.getX() - centroidX, 2.0);
		}
		return sum;
	}

	private static double moment02(Point[] pointSet, double centroidY) {
		double sum = 0.0;
		for (Point2D p : pointSet) {
			sum += Math.pow(p.getY() - centroidY, 2.0);
		}
		return sum;
	}

}
