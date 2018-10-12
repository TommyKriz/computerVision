package assignment04;

import java.awt.geom.Point2D;

public class PolarPoint {

	private Point2D p;
	private double distanceToCentroid;
	private double angleFromXAxis;

	public PolarPoint(Point2D p, double distanceToCentroid, double angleFromXAxis) {
		this.p = p;
		this.distanceToCentroid = distanceToCentroid;
		this.angleFromXAxis = angleFromXAxis;
	}

	public Point2D getP() {
		return p;
	}

	public double getDistanceToCentroid() {
		return distanceToCentroid;
	}

	public double getAngleFromXAxis() {
		return angleFromXAxis;
	}

	@Override
	public String toString() {
		return "--Point-- " + p.getX() + "|" + p.getY() + " d: " + distanceToCentroid + " a: " + angleFromXAxis;
	}

}
