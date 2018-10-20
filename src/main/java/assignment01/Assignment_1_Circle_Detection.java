package assignment01;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

public class Assignment_1_Circle_Detection implements PlugInFilter {

	/**
	 * The number of circles that are guessed.
	 */
	private static final int GUESSING_TRIALS = 1000;

	/**
	 * relevant for associating many points with a circle
	 */
	private static final double ASSOCIATION_RADIAL_OFFSET = 5.0001;

	/**
	 * the top X best results to show.
	 */
	private static final int NUMBER_OF_RESULTS_TO_SHOW = 5;

	/**
	 * expects a image created by the {@link Make_Circle_TestImage} plugin.
	 */
	public int setup(String arg, ImagePlus im) {
		return PlugInFilter.DOES_8G;
	}

	public void run(ImageProcessor ip) {

		List<Circle> guessedCircles = new ArrayList<>();

		CirclePointUtils pointStore = new CirclePointUtils(ip);

		for (int i = 0; i < GUESSING_TRIALS; i++) {
			try {
				Circle c = CircleMath.constructCircle(pointStore.getThreeRandomPoints());
				c.setAssociatedPoints(pointStore.associatePointsWithCircle(c, ASSOCIATION_RADIAL_OFFSET));
				guessedCircles.add(c);
			} catch (CircleConstructionException e) {
				IJ.log("Could not detect circle.");
			}
		}

		Collections.sort(guessedCircles, new Comparator<Circle>() {
			@Override
			public int compare(Circle c1, Circle c2) {
				return c1.getAssociatedPoints().size() - c2.getAssociatedPoints().size();
			}
		});
		Collections.reverse(guessedCircles);

		for (int i = 0; i < NUMBER_OF_RESULTS_TO_SHOW; i++) {
			plotCircleAndShowImage(ip.convertToColorProcessor(), guessedCircles.get(i));
		}

	}

	private void plotCircleAndShowImage(ColorProcessor cp, Circle c) {
		cp.setColor(Color.blue);
		// draw circle
		int cx = c.getCenter().x;
		int cy = c.getCenter().y;
		int r = (int) c.getRadius();
		cp.drawOval(cx - r, cy - r, r * 2, r * 2);
		// draw associated Points
		int rOval = 2;
		cp.setColor(Color.YELLOW);
		for (Point p : c.getAssociatedPoints()) {
			cp.drawPixel(p.x, p.y);
			cp.drawOval(p.x - rOval, p.y - rOval, rOval * 2, rOval * 2);
		}
		String circleDesc = "Center " + cx + "|" + cy + "radius: " + r + " # of Associated Points: "
				+ c.getAssociatedPoints().size();
		IJ.log(circleDesc);
		showImage(cp, circleDesc);
	}

	private void showImage(ImageProcessor ip, String title) {
		(new ImagePlus(title, ip)).show();
	}
}
