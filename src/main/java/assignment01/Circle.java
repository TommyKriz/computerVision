package assignment01;

import java.awt.Point;
import java.util.List;

public class Circle {

	private Point center;

	private double radius;

	private List<Point> associatedPoints = null;

	public Circle(Point center, double radius) {
		this.center = center;
		this.radius = radius;
	}

	public Point getCenter() {
		return center;
	}

	public double getRadius() {
		return radius;
	}

	public List<Point> getAssociatedPoints() {
		return associatedPoints;
	}

	public void setAssociatedPoints(List<Point> associatedPoints) {
		this.associatedPoints = associatedPoints;
	}

}
