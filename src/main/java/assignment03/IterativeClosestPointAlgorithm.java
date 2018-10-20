package assignment03;

import java.awt.Point;

import assignment02.AffineTransform;

public class IterativeClosestPointAlgorithm {

	Point[] points1;
	Point[] points2;

	public IterativeClosestPointAlgorithm(Point[] points1, Point[] points2) {
		this.points1 = points1;
		this.points2 = points2;
	}

	private AffineTransform initialTransform() {
		Point centroid1 = calcCentroid(points1);
		Point centroid2 = calcCentroid(points2);
		return new AffineTransform(centroid2.x - centroid1.x, 0, 0, 0,
				centroid2.y - centroid1.y, 0);
	}

	private Point calcCentroid(Point[] points) {
		int x = 0, y = 0;
		for (Point p : points) {
			x += p.x;
			y += p.y;
		}
		x /= points.length;
		y /= points.length;
		return new Point(x, y);
	}

}
