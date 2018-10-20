package assignment01;

import java.awt.Point;

class CircleConstructionException extends IllegalArgumentException {
	private static final long serialVersionUID = 1L;
}

public class CircleMath {

	private static final double TOL = 0.0000001;

	/**
	 * Algorithm from
	 * <a href="http://paulbourke.net/geometry/circlesphere/Circle.cpp">Here</a>
	 * .
	 */
	public static Circle constructCircle(Point[] threePoints) throws CircleConstructionException {

		final Point p1 = threePoints[0];
		final Point p2 = threePoints[1];
		final Point p3 = threePoints[2];

		double yDelta_a = p2.y - p1.y;
		double xDelta_a = p2.x - p1.x;
		double yDelta_b = p3.y - p2.y;
		double xDelta_b = p3.x - p2.x;

		if (Math.abs(xDelta_a) <= TOL && Math.abs(yDelta_b) <= TOL) {
			throw new CircleConstructionException();
		}

		double aSlope = yDelta_a / xDelta_a;
		double bSlope = yDelta_b / xDelta_b;
		if (Math.abs(aSlope - bSlope) <= TOL) {
			throw new CircleConstructionException();
		}

		// calc center
		double cx = (aSlope * bSlope * (p1.y - p3.y) + bSlope * (p1.x + p2.x) - aSlope * (p2.x + p3.x))
				/ (2 * (bSlope - aSlope));
		double cy = -1 * (cx - (p1.x + p2.x) / 2) / aSlope + (p1.y + p2.y) / 2;

		Point center = new Point((int) cx, (int) cy);

		double r = center.distance(p1.getX(), p1.getY());
		return new Circle(center, r);
	}

}
