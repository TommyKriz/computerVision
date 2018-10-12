package assignment04;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class PointAggregator {
	private List<Point2D> points = new ArrayList<>();

	public void add(Point2D p) {
		points.add(p);
	}

	public List<Point2D> getPoints() {
		return points;
	}
}
