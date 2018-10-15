package assignment04;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ColorProcessor;
import imagingbook.lib.color.RandomColorGenerator;
import imagingbook.pub.regions.Contour;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class CPDH_Graphic {

	private static final int NUMBER_OF_RADIAL_SEGMENTS = 3;
	private static final int NUMBER_OF_ANGULAR_SEGMENTS = 12;

	/**
	 * each field represents a segment, its value represents the number of
	 * contour points within that segment.
	 */
	private int[][] histogram = new int[NUMBER_OF_RADIAL_SEGMENTS][NUMBER_OF_ANGULAR_SEGMENTS];

	private double rMax;

	// in rad
	private double mainAxis;

	public CPDH_Graphic(ColorProcessor cp, Contour c) {
		Point2D centroid = calcCentroid(c);
		initMaxRAndMainAxis(c, centroid);
		fillHistogramAndDrawPointsPerSegment(cp, associatePolarCoordinates(c, centroid));
		drawPolarGrid(cp, centroid);
		new ImagePlus("CPDH", cp).show();
	}

	private void fillHistogramAndDrawPointsPerSegment(ColorProcessor cp, List<PolarPoint> polarPoints) {

		// helper array
		PointAggregator[][] histogramWithPoints = new PointAggregator[NUMBER_OF_RADIAL_SEGMENTS][NUMBER_OF_ANGULAR_SEGMENTS];
		for (int i = 0; i < NUMBER_OF_RADIAL_SEGMENTS; i++) {
			for (int j = 0; j < NUMBER_OF_ANGULAR_SEGMENTS; j++) {
				histogramWithPoints[i][j] = new PointAggregator();
			}
		}

		for (PolarPoint p : polarPoints) {
			int rad_idx = calcRadialIdx(p.getDistanceToCentroid());
			int ang_idx = calcAngularIdx(p.getAngleFromXAxis());
			histogram[rad_idx][ang_idx]++;
			histogramWithPoints[rad_idx][ang_idx].add(p.getP());
		}

		RandomColorGenerator rcg = new RandomColorGenerator();
		for (int i = 0; i < NUMBER_OF_RADIAL_SEGMENTS; i++) {
			for (int j = 0; j < NUMBER_OF_ANGULAR_SEGMENTS; j++) {
				cp.setColor(rcg.nextColor());
				for (Point2D p : histogramWithPoints[i][j].getPoints()) {
					cp.drawPixel((int) p.getX(), (int) p.getY());
				}
			}
		}

	}

	public int[][] getHistogram() {
		return histogram;
	}

	private Point2D calcCentroid(Contour contour) {
		double sumX = 0.0;
		double sumY = 0.0;
		for (Point2D p : contour) {
			sumX += p.getX();
			sumY += p.getY();
		}
		return new Point2D.Double(sumX / contour.getLength(), sumY / contour.getLength());
	}

	private void initMaxRAndMainAxis(Contour outerContour, Point2D centroid) {
		Point2D mostDistantPoint = null;
		double maxDistance = 0.0;
		double d;
		for (Point2D p : outerContour) {
			d = p.distance(centroid);
			if (d > maxDistance) {
				maxDistance = d;
				mostDistantPoint = p;
			}
		}
		mainAxis = angleFromXAxis(mostDistantPoint, centroid);
		
		// TODO:
//		majorAxis = PointSetMath.getMajorAxis(outerContour.getPointArray());
//		IJ.log("Major Axis: " + majorAxis + " in deg: "
//				+ Math.toDegrees(majorAxis));
		
		rMax = maxDistance;
	}

	/**
	 * Offsets a from the main Axis so that e.g.:
	 * 
	 * For a main Axis of 65°, an a of 64° would fall into the last angular
	 * segment.
	 * 
	 * @param a
	 *            angle in radians
	 * @return the angular segment index
	 */
	private int calcAngularIdx(double a) {
		double TWO_PI = Math.PI * 2;
		// offset a
		if (a > mainAxis) {
			a -= mainAxis;
		} else if (a < mainAxis) {
			a = (TWO_PI - (mainAxis - a));
		}
		return (int) (a / (TWO_PI / NUMBER_OF_ANGULAR_SEGMENTS));
	}

	private int calcRadialIdx(double distanceToCentroid) {
		// TODO: get rid of if clause
		if (distanceToCentroid >= rMax) {
			IJ.log("Special case, point is exactly rMax away from centroid");
			return NUMBER_OF_RADIAL_SEGMENTS - 1;
		}
		return (int) (distanceToCentroid / (rMax / NUMBER_OF_RADIAL_SEGMENTS));

	}

	private List<PolarPoint> associatePolarCoordinates(Contour contour, Point2D centroid) {
		List<PolarPoint> result = new ArrayList<>();
		for (Point2D p : contour) {
			result.add(new PolarPoint(p, p.distance(centroid), angleFromXAxis(p, centroid)));
		}
		return result;
	}

	/**
	 * @return angle from xAxis in radians in the range of [0,2*PI]
	 */
	private double angleFromXAxis(Point2D p, Point2D centroid) {
		// shift the range from [-PI,PI] to [0,2*PI]
		return Math.atan2((p.getY() - centroid.getY()), (p.getX() - centroid.getX())) + Math.PI;
	};

	private void drawPolarGrid(ColorProcessor cp, Point2D centroid) {

		final double cx = centroid.getX();
		final double cy = centroid.getY();

		cp.setColor(new Color(12, 200, 200));

		drawRadialSegments(cx, cy, cp);

		drawAngularSegments(cx, cy, cp);

	}

	private void drawAngularSegments(double cx, double cy, ColorProcessor cp) {
		double TWO_PI = 2 * Math.PI;
		double a = mainAxis;
		double angularSegmentWidth = TWO_PI / NUMBER_OF_ANGULAR_SEGMENTS;
		while ((a + angularSegmentWidth) <= 360) {
			a += angularSegmentWidth;
			drawAngularLine(cx, cy, a, cp);
		}
		a = TWO_PI - a;
		while ((a + angularSegmentWidth) < mainAxis) {
			a += angularSegmentWidth;
		}
	}

	private void drawAngularLine(double cx, double cy, double a, ColorProcessor cp) {
		cp.drawLine((int) cx, (int) cy, (int) (cx + rMax * Math.cos(a)), (int) (cy + rMax * Math.sin(a)));
	}

	private void drawRadialSegments(double cx, double cy, ColorProcessor cp) {
		cp.drawOval((int) (cx - rMax), (int) (cy - rMax), (int) rMax * 2, (int) rMax * 2);
		double segmentRadius = rMax / NUMBER_OF_RADIAL_SEGMENTS;
		for (int i = 1; i < NUMBER_OF_RADIAL_SEGMENTS; i++) {
			double r = segmentRadius * i;
			cp.drawOval((int) (cx - r), (int) (cy - r), (int) r * 2, (int) r * 2);
		}
	}

}