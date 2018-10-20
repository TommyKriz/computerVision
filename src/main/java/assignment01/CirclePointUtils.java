package assignment01;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

import ij.process.ImageProcessor;

public class CirclePointUtils {

	Point[] points;

	/**
	 * @param ip
	 *            is an image created with the {@link Make_Circle_TestImage}
	 *            plugin.
	 */
	public CirclePointUtils(ImageProcessor ip) {
		points = collectPoints(ip);
	}

	/**
	 * Iterate over the image and collect all non-black pixel coordinates.
	 * 
	 * @param ip
	 *            is an image created with the {@link Make_Circle_TestImage}
	 *            plugin.
	 * @return list of {@link Point}
	 */
	private Point[] collectPoints(ImageProcessor ip) {
		List<Point> pointList = new ArrayList<>();
		// iterate over all the pixels
		for (int y = 0; y < ip.getHeight(); y++) {
			for (int x = 0; x < ip.getWidth(); x++) {
				if (ip.getPixel(x, y) > 0) {
					pointList.add(new Point(x, y));
				}
			}
		}
		// convert the list to an array
		return pointList.toArray(new Point[0]);
	}

	public Point[] getThreeRandomPoints() {
		Point[] threeRandomPoints = new Point[3];
		// prevent choosing the same point twice
		Stack<Integer> alreadyUsedIdx = new Stack<>();
		int randIdx = randomPointIndex();
		for (int i = 0; i < 3; i++) {
			while (alreadyUsedIdx.contains(randIdx)) {
				randIdx = randomPointIndex();
			}
			alreadyUsedIdx.push(randIdx);
			threeRandomPoints[i] = points[randIdx];
		}
		return threeRandomPoints;
	}

	private int randomPointIndex() {
		return ThreadLocalRandom.current().nextInt(0, points.length);
	}

	public List<Point> associatePointsWithCircle(Circle c, double associationThreshold) {
		List<Point> associatedPoints = new ArrayList<>();

		double outerOffset = c.getRadius() + associationThreshold;
		double innerOffset = c.getRadius() - associationThreshold;

		double cx = c.getCenter().x;
		double cy = c.getCenter().y;

		for (Point p : points) {
			double distanceFromCircleCenter = p.distance(cx, cy);
			if (distanceFromCircleCenter > innerOffset && distanceFromCircleCenter < outerOffset) {
				associatedPoints.add(p);
			}
		}
		return associatedPoints;
	}

}
